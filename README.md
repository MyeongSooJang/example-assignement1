# 백엔드 과제 연습

### 1. 프로젝트 개요
 - 학습 플랫폼 대표기능인 단원별 문제 풀이' 및 '풀이 이력 조회' API를 구축

### 2. 기술 스택

| 분류 | 기술 |
|---|---|
| Language | Java 21 |
| Framework | Spring Boot 4.1.0 |
| ORM | Spring Data JPA |
| Database | MySQL |
| Test DB | H2 |
| Build | Gradle (멀티모듈) |
| Test | JUnit 5 |
| 기타 | Lombok |

### 3. 실행 방법

### 4. 모듈 구조

```
domain          ← 순수 Java. 비즈니스 규칙과 도메인 모델
application     ← 유스케이스 조합. Spring 의존 없음
infrastructure  ← JPA, MySQL 구현체
api             ← REST 컨트롤러, DTO, 예외 처리
```

의존 방향: `api` → `application` → `domain` ← `infrastructure`

#### domain
| 패키지 | 역할 |
|---|---|
| `problem` | 문제 도메인 모델. 채점(`evaluate`), 정답 생성, 복수정답 여부 판별 |
| `answer` | 답안 인터페이스 및 구현체 (객관식/주관식) |
| `history` | 사용자 풀이 이력 도메인 생성 및 업데이트 |
| `repository` | 저장소 인터페이스 (구현은 infrastructure) |
| `service` | 도메인 서비스 (`CorrectRateCalculator`) |
| `exception` | 도메인 예외 |

#### application
| 패키지 | 역할 |
|---|---|
| `problem` | 유스케이스: 랜덤 문제 조회, 답안 제출, 이력 조회 |
| `common` | 공통 검증 (UserValidator, ChapterValidator) |

#### infrastructure
| 패키지 | 역할 |
|---|---|
| `entity` | JPA 엔티티 (JOINED 상속 전략) |
| `repository` | 도메인 저장소 인터페이스 구현체 |
| `converter` | JPA 커스텀 컨버터 |

#### api
| 패키지 | 역할 |
|---|---|
| `problem` | REST 컨트롤러 및 요청/응답 DTO |
| `exception` | GlobalExceptionHandler, 표준 에러 응답 포맷 |

### 5. API 명세

#### 랜덤 문제 조회
```
GET /api/problems/random?chapterId=1&userId=1
```
**Response**
```json
{
  "problemId": 1,
  "content": "대한민국의 수도는?",
  "choices": ["서울", "부산", "대구", "인천", "광주"],
  "answerCorrectRate": 87
}
```
- 주관식 문제의 경우 `choices`는 `null`
- 해당 단원의 풀 문제가 없으면 `204 No Content` 반환
- `answerCorrectRate`는 30명 이상이 푼 문제에만 제공, 미만이면 `null`

---

#### 답안 제출
```
POST /api/problems/{problemId}/submit
```
**Request (객관식)**
```json
{
  "userId": 1,
  "answerType": "MULTIPLE_CHOICE",
  "selectedChoices": [1, 3]
}
```
**Request (주관식)**
```json
{
  "userId": 1,
  "answerType": "SHORT_ANSWER",
  "text": "서울"
}
```
**Response**
```json
{
  "answerStatus": "CORRECT",
  "explanation": "서울은 1394년부터 대한민국의 수도입니다."
}
```
- `answerStatus`: `CORRECT` / `PARTIAL` / `WRONG`
- 복수 정답 문제에서 정답을 1개라도 포함하면 `PARTIAL`

---

#### 풀이 이력 조회
```
GET /api/problems/{problemId}/history?userId={userId}
```
**Response (객관식)**
```json
{
  "problemId": 1,
  "answerStatus": "PARTIAL",
  "explanation": "문제 해설...",
  "problemAnswers": [1, 3],
  "userAnswers": [1, 2],
  "answerCorrectRate": 67
}
```
**Response (주관식)**
```json
{
  "problemId": 2,
  "answerStatus": "CORRECT",
  "explanation": "문제 해설...",
  "problemAnswers": "서울",
  "userAnswers": "서울",
  "answerCorrectRate": null
}
```