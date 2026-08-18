# assignment1 백엔드 과제 설계 문서

> 이 문서는 대화를 통해 결정된 설계를 반영한다. plan 파일보다 이 문서가 우선한다.

## 개요

학습 플랫폼의 '단원별 문제 풀이' 및 '풀이 이력 조회' API 구축.
객체지향적 설계와 DDD 원칙을 중심으로 구현한다.

**기술 스택:** Java 21, Spring Boot 4.1.0, JPA, MySQL

---

## 1. 아키텍처: DDD 헥사고날 멀티 모듈

```
assignment1/
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
├── MultipleChoiceProblem   ← choices: List<String>, correctAnswerSet: Set<Integer>
└── ShortAnswerProblem      ← correctAnswer: String
```

**Problem의 책임:**
- `evaluate(MultipleChoiceAnswer)` / `evaluate(ShortAnswer)` → AnswerStatus 반환 (오버로딩)
- `isMultipleAnswer()` → 복수정답 여부 (MultipleChoiceProblem)

### 답안 (Answer)

```
Answer (인터페이스)
├── toText(): String         ← 저장/표시용 문자열 변환
├── MultipleChoiceAnswer     ← selectedChoices: List<Integer>, toText() → "1,3"
└── ShortAnswer              ← text: String, toText() → "파리"
```

- Answer 객체는 채점 시점에만 사용된다
- 저장 시점에 `toText()`로 변환해서 String으로 보관한다

### 정답 상태 (AnswerStatus)

| 상태 | 조건 |
|---|---|
| `CORRECT` | 제출한 답이 정답과 완전히 일치 |
| `PARTIAL` | 정답을 1개 이상 포함하지만 오답도 포함 |
| `WRONG` | 정답을 하나도 포함하지 않음 |

부분 정답 예시: 정답이 `[1, 2]`일 때 `[1, 3]` 제출 → `PARTIAL`

### 풀이 이력 (UserProblemHistory)

```
UserProblemHistory
├── userId: Long
├── problemId: Long
├── answerStatus: AnswerStatus
└── userAnswer: String       ← Answer.toText()로 변환해서 저장
```

- `userAnswer`는 Answer 객체가 아닌 String으로 저장한다
- 이력은 이미 일어난 사실의 기록이므로 도메인 객체 참조를 갖지 않는다

### 건너뛰기 이력 (UserChapterSkip)

```
UserChapterSkip
├── userId: Long
├── chapterId: Long
└── skippedProblemId: Long   ← 직전 건너뛴 문제 ID
```

- (userId, chapterId) 기준으로 1개만 유지 (건너뛸 때마다 upsert)
- 랜덤 문제 조회 시 이 값을 참조해 해당 문제를 후보에서 제외

### 사용자 (User)

- 인증 없음. seed 데이터로 관리.
- `id`, `name` 필드만 존재.

---

## 3. Domain Service

```
domain/service/
└── CorrectRateCalculator    ← 정답률 계산 Domain Service
```

**CorrectRateCalculator의 책임:**
- 30명 이상일 때만 정답률 반환, 미만이면 `Optional.empty()`
- 소수점 첫째 자리에서 반올림 (예: 66.7% → 67%)
- 부분 정답(PARTIAL)은 오답으로 간주

---

## 4. 핵심 비즈니스 규칙

### 랜덤 문제 조회 규칙

1. 해당 단원에서 사용자가 아직 풀지 않은 문제 중 랜덤 1개 선택
2. 직전에 건너뛴 문제(`skippedProblemId`)는 후보에서 제외
3. 풀 수 있는 문제가 없으면 예외 처리

### 정답률 계산 규칙

- 해당 문제를 푼 사용자가 30명 이상일 때만 정답률 반환
- 30명 미만이면 `null` 반환
- 부분 정답(`PARTIAL`)은 오답으로 간주
- 소수점 첫째 자리에서 반올림 (예: 66.7% → 67%)

---

## 5. 유스케이스 (application 레이어)

### GetRandomProblemUseCase

```
1. chapterId로 Chapter 조회
2. userId로 해당 단원에서 이미 푼 문제 ID 목록 조회
3. 직전 건너뛴 문제 ID 조회
4. 풀지 않은 문제 중 건너뛴 문제 제외 후 랜덤 1개 선택
5. CorrectRateCalculator로 정답률 계산
6. 결과 반환
```

### SubmitAnswerUseCase

```
1. problemId로 Problem 조회
2. 요청 타입에 맞게 Answer 생성
3. problem.evaluate(answer) 호출 → AnswerStatus
4. UserProblemHistory.create(userId, problemId, answerStatus, answer) 저장
5. 정답 여부 + 해설 반환
```

### GetProblemHistoryUseCase

```
1. userId + problemId로 UserProblemHistory 조회
2. 문제 정답, 사용자 답안, 정답률, 해설 조합
3. 결과 반환
```

---

## 6. API 설계

### 랜덤 문제 조회

```
POST /api/problems/random

Request:
{ "chapterId": 1, "userId": 1 }

Response:
{
  "problemId": 1,
  "content": "문제 설명",
  "choices": ["지문1", "지문2", "지문3", "지문4", "지문5"],
  "answerCorrectRate": 67   // 30명 미만이면 null
}
```

### 문제 제출

```
POST /api/problems/{problemId}/submit

Request (객관식):
{ "userId": 1, "answerType": "MULTIPLE_CHOICE", "userAnswer": [1, 3] }

Request (주관식):
{ "userId": 1, "answerType": "SHORT_ANSWER", "userAnswer": "서울" }

Response:
{ "answerStatus": "PARTIAL", "explanation": "문제 해설...", "correctAnswers": [1, 2] }
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
  "userAnswers": "1,2",
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

## 7. infrastructure 설계

### JPA 상속 전략

- `Problem`: **JOINED** 전략
  - `problem` 공통 테이블 + `multiple_choice_problem` / `short_answer_problem` 테이블 분리

### Repository 패턴

```
domain/repository/ProblemRepository          ← 인터페이스 (Spring 모름)
infrastructure/ProblemJpaRepository          ← Spring Data JPA
infrastructure/ProblemRepositoryImpl         ← domain 인터페이스 구현, JpaEntity ↔ Domain 변환
```

### UserProblemHistory 저장

`user_problem_history` 테이블에 `answer_type` + `user_answer(String)` 컬럼으로 저장.
- 객관식: `answer_type = "MULTIPLE_CHOICE"`, `user_answer = "1,3"`
- 주관식: `answer_type = "SHORT_ANSWER"`, `user_answer = "서울"`
