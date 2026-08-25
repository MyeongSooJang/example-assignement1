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
**Query Parameter**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `chapterId` | Long | 단원 ID |
| `userId` | Long | 사용자 ID |

**Response (객관식)**
```json
{
  "problemId": 1,
  "content": "대한민국의 수도는?",
  "choices": ["서울", "부산", "대구", "인천", "광주"],
  "answerCorrectRate": 87
}
```
**Response (주관식)**
```json
{
  "problemId": 2,
  "content": "대한민국의 수도를 작성하시오.",
  "choices": null,
  "answerCorrectRate": null
}
```
- 해당 단원의 풀 문제가 없으면 `204 No Content` 반환
- `answerCorrectRate`는 30명 이상이 푼 문제에만 제공, 미만이면 `null`

---

#### 답안 제출
```
POST /api/problems/{problemId}/submit
```
**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `problemId` | Long | 제출할 문제 ID |

**Request Body**

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `userId` | Long | O | 사용자 ID |
| `answerType` | String | O | `MULTIPLE_CHOICE` / `SHORT_ANSWER` |
| `selectedChoices` | List&lt;Integer&gt; | 객관식만 | 선택한 선택지 번호 (1~5) |
| `text` | String | 주관식만 | 주관식 답안 텍스트 |

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
**Query Parameter**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `problemId` | Long | 조회할 문제 ID (Path Variable) |
| `userId` | Long | 사용자 ID |
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

### 6. 설계 결정

#### domain 모듈을 순수 Java로 유지

domain 모듈에 Spring 의존성을 넣지 않았다. Spring이 있으면 테스트 실행 시 Spring 컨텍스트와 JPA가 초기화되어야 하기 때문에, 
단순한 채점 로직 하나를 테스트하려고 해도 DB 연결과 같은 외부 환경이 필요해진다. 
domain을 순수 Java로 유지하면 Spring 컨텍스트와 JPA 없이 비즈니스 로직만 독립적으로 테스트할 수 있다.

#### JPA 상속 전략으로 JOINED 선택

클라이언트가 답안 제출 시 `answerType`을 함께 보내주지만, 서버는 이를 신뢰하지 않는다. 
클라이언트가 실제로는 객관식인 문제에 `SHORT_ANSWER` 타입을 보내는 경우, 타입을 믿고 조회하면 잘못된 채점이 일어난다. 
따라서 `problemId` 하나만으로 서버가 직접 문제 타입을 판단할 수 있어야 하고, 
이를 위해 부모 타입(`ProblemEntity`)으로 다형성 조회가 가능한 JOINED 전략을 선택했다.

#### 정답률 계산을 Domain Service로 분리

처음에는 정답률을 `Problem` 안에서 계산하려 했다. 하지만 정답률은 문제 자체의 속성이 아니라 풀이 이력 데이터를 집계한 결과다.
`Problem`이 풀이 이력을 알아야 계산할 수 있다는 것은 `Problem`의 책임 범위를 벗어난다.
별도 Domain Service(`CorrectRateCalculator`)로 분리해 각 객체가 자신의 책임만 수행하도록 설계했다.

#### 정답률 계산을 DB 집계 쿼리로 처리

초기 구현에서는 전체 풀이 이력을 메모리에 올려 Java에서 직접 카운트했다. 특정 문제를 푼 사용자가 많아질수록 불필요하게 많은 데이터를 메모리에 올리는 문제가 있었다.
이를 DB 집계 쿼리(`COUNT`)로 변경해 숫자 두 개만 받아오도록 개선했다. 데이터 집계는 DB가, 비즈니스 규칙(30명 기준 판단, 비율 계산)은 Domain Service가 담당하도록 역할을 분리했다.