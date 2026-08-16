# jangsomoa 백엔드 과제 설계 문서

## 개요

학습 플랫폼의 '단원별 문제 풀이' 및 '풀이 이력 조회' API 구축.
객체지향적 설계와 DDD 원칙을 중심으로 구현한다.

**기술 스택:** Java 21, Spring Boot 4.1.0, JPA, MySQL

---

## 1. 아키텍처: DDD 헥사고날 멀티 모듈

```
jangsomoa/
├── domain/          ← 순수 Java. Spring/JPA 의존 없음. 핵심 비즈니스 규칙
├── application/     ← 유스케이스 조율. domain 호출
├── infrastructure/  ← JPA 구현체, DB 설정. domain 인터페이스 구현
└── api/             ← Controller, DTO, Swagger. 애플리케이션 진입점
```

**의존성 방향:** `api → application → domain ← infrastructure`

- `domain`은 아무것도 의존하지 않는다
- `infrastructure`가 `domain`의 Repository 인터페이스를 구현한다 (의존성 역전)
- `domain`은 Spring 없이 순수 JUnit으로 테스트 가능하다

---

## 2. 도메인 모델

### 문제 (Problem)

```
Problem (추상)
├── MultipleChoiceProblem   ← choices: List<String> (5지선다)
└── ShortAnswerProblem
```

- `Problem`에는 JPA 어노테이션이 없다. DB 매핑은 `infrastructure`의 JPA Entity가 담당한다.
- 정답 판정 메서드 `evaluate(UserAnswer): AnswerStatus` 를 각 구현체가 오버라이드한다.

### 사용자 답안 (UserAnswer)

```
UserAnswer (추상)
├── MultipleChoiceAnswer    ← selectedChoices: List<Integer>
└── ShortAnswer             ← text: String
```

- `Problem`과 `UserAnswer`가 타입 대칭을 이룬다.
- `MultipleChoiceProblem.evaluate(MultipleChoiceAnswer)`, `ShortAnswerProblem.evaluate(ShortAnswer)` 형태로 타입 안전하게 판정한다.

### 정답 상태 (AnswerStatus)

| 상태 | 조건 |
|---|---|
| `CORRECT` | 제출한 답이 정답과 완전히 일치 |
| `PARTIAL` | 정답을 1개 이상 포함하지만 오답도 포함 |
| `INCORRECT` | 정답을 하나도 포함하지 않음 |

부분 정답 예시: 정답이 `[1, 2]`일 때 `[1, 3]` 제출 → `PARTIAL`

### 풀이 이력 (UserProblemHistory)

```
UserProblemHistory
├── problem: Problem
├── user: User
├── userAnswer: UserAnswer
└── answerStatus: AnswerStatus
```

### 건너뛰기 이력 (UserChapterSkip)

문제를 건너뛰면 풀이 이력이 생기지 않으므로 별도 엔티티로 관리한다.

```
UserChapterSkip
├── user: User
├── chapter: Chapter
└── skippedProblemId: Long   ← 직전 건너뛴 문제 ID
```

- (userId, chapterId) 기준으로 1개만 유지 (건너뛸 때마다 upsert)
- 랜덤 문제 조회 시 이 값을 참조해 해당 문제를 후보에서 제외

### 사용자 (User)

- 인증 없음. seed 데이터로 관리.
- `id`, `name` 필드만 존재.

---

## 3. 핵심 비즈니스 규칙

### 랜덤 문제 조회 규칙

1. 해당 단원에서 사용자가 아직 풀지 않은 문제 중 랜덤 1개 선택
2. 직전에 건너뛴 문제(`skippedProblemId`)는 후보에서 제외
3. 풀 수 있는 문제가 없으면 예외 처리

### 정답률 계산 규칙

- 해당 문제를 푼 사용자가 30명 이상일 때만 정답률 반환
- 30명 미만이면 `null` 반환
- 부분 정답(`PARTIAL`)은 정답률 계산에서 오답으로 간주
- 소수점 첫째 자리에서 반올림 (예: 66.7% → 67%)

---

## 4. 유스케이스 (application 레이어)

### GetRandomProblemUseCase

```
1. chapterId로 Chapter 조회
2. userId로 해당 단원에서 이미 푼 문제 ID 목록 조회
3. 직전 건너뛴 문제 ID 조회
4. 풀지 않은 문제 중 건너뛴 문제 제외 후 랜덤 1개 선택
5. 해당 문제의 정답률 계산
6. 결과 반환
```

### SubmitAnswerUseCase

```
1. problemId로 Problem 조회
2. 요청 타입에 맞게 UserAnswer 생성
3. problem.evaluate(userAnswer) 호출 → AnswerStatus
4. UserProblemHistory 저장
5. 정답 여부 + 해설 반환
```

### GetProblemHistoryUseCase

```
1. userId + problemId로 UserProblemHistory 조회
2. 문제 정답, 사용자 답안, 정답률, 해설 조합
3. 결과 반환
```

---

## 5. API 설계

### 랜덤 문제 조회

```
POST /api/problems/random

Request:
{
  "chapterId": 1,
  "userId": 1
}

Response:
{
  "problemId": 1,
  "content": "문제 설명",
  "choices": ["지문1", "지문2", "지문3", "지문4", "지문5"],  // 객관식만
  "answerCorrectRate": 67  // 30명 미만이면 null
}
```

### 문제 제출

```
POST /api/problems/{problemId}/submit

Request (객관식):
{
  "userId": 1,
  "answerType": "MULTIPLE_CHOICE",
  "userAnswer": [1, 3]
}

Request (주관식):
{
  "userId": 1,
  "answerType": "SHORT_ANSWER",
  "userAnswer": "서울"
}

Response:
{
  "answerStatus": "PARTIAL",
  "explanation": "문제 해설...",
  "correctAnswers": [1, 2]
}
```

### 풀었던 문제 상세 조회

```
GET /api/problems/{problemId}/history?userId={userId}

Response:
{
  "problemId": 1,
  "answerStatus": "CORRECT",
  "explanation": "문제 해설...",
  "problemAnswers": [1, 2],
  "userAnswers": [1, 2],
  "answerCorrectRate": 67
}
```

### 예외 처리

| 상황 | HTTP 상태 |
|---|---|
| 해당 단원 풀 문제 없음 | `204 No Content` |
| 존재하지 않는 문제/유저 | `404 Not Found` |
| 잘못된 요청 형식 | `400 Bad Request` |
| 아직 풀지 않은 문제 상세 조회 | `404 Not Found` |

---

## 6. infrastructure 설계

### JPA 상속 전략

- `Problem`: **JOINED** 전략
  - `problem` 공통 테이블 + `multiple_choice_problem` / `short_answer_problem` 테이블 분리
  - 객관식/주관식 컬럼 차이가 명확해 정규화된 구조가 적합

### Repository 패턴

```
domain/ProblemRepository          ← 인터페이스 (Spring 모름)
infrastructure/ProblemJpaRepository    ← Spring Data JPA
infrastructure/ProblemRepositoryImpl   ← domain 인터페이스 구현, JpaEntity ↔ Domain 변환
```

### UserAnswer 저장

`UserProblemHistory` 테이블에 `answer_type` + `answer_value(JSON)` 컬럼으로 저장.
- 객관식: `answer_type = "MULTIPLE_CHOICE"`, `answer_value = "[1,3]"`
- 주관식: `answer_type = "SHORT_ANSWER"`, `answer_value = "서울"`

---

## 7. 테스트 전략

### domain 단위 테스트 (Spring 없음)

- `AnswerEvaluatorTest`: 정답/부분정답/오답 각 케이스
- `CorrectRateCalculatorTest`: 30명 이상/미만 케이스

### application 단위 테스트 (Repository Mock)

- `GetRandomProblemUseCaseTest`: 정상 조회, 건너뛴 문제 제외, 풀 문제 없음 예외
- `SubmitAnswerUseCaseTest`: 저장 확인, 각 정답 상태 반환값

### infrastructure 통합 테스트 (@DataJpaTest + H2)

- `ProblemRepositoryImplTest`: 풀지 않은 문제 조회, domain 객체 변환

### api 통합 테스트 (@SpringBootTest + MockMvc)

- `ProblemControllerTest`: 정상 응답, 예외 응답(204, 400, 404)
