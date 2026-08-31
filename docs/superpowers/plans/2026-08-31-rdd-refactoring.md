# RDD 기반 전체 레이어 리팩터링 구현 계획

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 책임 주도 설계(RDD) 원칙에 따라 3개 유스케이스 시나리오를 기준으로 각 객체의 책임을 재정의하고 코드에 반영한다.

**Architecture:** 유스케이스 시나리오 흐름에서 어떤 메시지가 어떤 객체에 전달되는지를 추적해 책임을 배분한다. 사용자가 책임을 먼저 제안하고, 검토 후 코드로 옮기는 대화 주도 방식으로 진행한다. domain → application → api 순으로 올라간다.

**Tech Stack:** Java 21, Spring Boot, JPA, Gradle 멀티모듈

## Global Constraints

- domain 모듈: Spring/JPA 어노테이션 절대 금지, 순수 Java만 사용
- 변수명 축약 금지 (mca → multipleChoiceAnswer)
- 삼항 연산자 사용 금지, if/else로 작성
- 주석 작성 금지
- 테스트 메서드명은 한글로 시나리오 명확히 작성
- 완료 기준: `./gradlew test` 통과

---

## Task 1: 랜덤 문제 조회 — RDD 책임 설계 및 코드 반영

**시나리오:** 사용자가 단원을 선택하면 풀지 않은 문제 중 하나를 랜덤으로 제공한다. 직전 건너뛴 문제는 제외. 정답률은 30명 이상 푼 경우에만 제공.

**관여 객체:**
- `Problem` (domain) — 문제 자체의 정보와 행동
- `UserChapterSkip` (domain) — 건너뛰기 추적
- `UserProblemHistory` (domain) — 풀이 이력
- `CorrectRateCalculator` (domain service) — 정답률 계산
- `GetRandomProblemService` (application) — 유스케이스 조율

**Files:**
- Modify: `domain/src/main/java/com/jms/assignment1/problem/Problem.java`
- Modify: `domain/src/main/java/com/jms/assignment1/problem/MultipleChoiceProblem.java`
- Modify: `domain/src/main/java/com/jms/assignment1/problem/ShortAnswerProblem.java`
- Modify: `domain/src/main/java/com/jms/assignment1/chapter/UserChapterSkip.java`
- Modify: `domain/src/main/java/com/jms/assignment1/service/CorrectRateCalculator.java`
- Modify: `application/src/main/java/com/jms/assignment1/application/problem/GetRandomProblemService.java`
- Modify: `domain/src/test/java/com/jms/assignment1/problem/MultipleChoiceProblemTest.java`
- Modify: `domain/src/test/java/com/jms/assignment1/problem/ShortAnswerProblemTest.java`

**진행 방식 (대화 주도):**

- [ ] **Step 1: 시나리오 흐름 확인**

  아래 흐름을 같이 확인한다:
  ```
  사용자 요청 (chapterId, userId)
    → 유저/챕터 존재 검증
    → 이미 푼 문제 ID 목록 조회
    → 직전 건너뛴 문제 ID 조회
    → 제외 목록 합산
    → 챕터 내 가용 문제 목록 조회
    → 랜덤 1개 선택
    → 정답률 계산
    → 응답 반환
  ```

- [ ] **Step 2: 객체별 책임 대화 — Problem**

  사용자가 `Problem`의 책임을 제안한다. RDD 원칙 기준으로 검토 후 합의한다.
  검토 포인트: `createAnswer()` 메서드가 Problem에 있는 것이 자연스러운가?

- [ ] **Step 3: 객체별 책임 대화 — UserChapterSkip**

  사용자가 `UserChapterSkip`의 책임을 제안한다.
  검토 포인트: 건너뛰기 업데이트 책임은 누가 갖는가?

- [ ] **Step 4: 객체별 책임 대화 — GetRandomProblemService**

  사용자가 `GetRandomProblemService`의 책임을 제안한다.
  검토 포인트: 가용 문제 필터링 로직이 서비스에 있는 것이 적절한가?

- [ ] **Step 5: 합의된 책임을 코드에 반영**

  대화에서 결정된 책임 배분에 따라 코드를 수정한다.

- [ ] **Step 6: 영향받은 테스트 수정 및 추가**

  ```bash
  ./gradlew :domain:test
  ```
  Expected: BUILD SUCCESSFUL

- [ ] **Step 7: 커밋**

  ```bash
  git add -p
  git commit -m "refactor: 랜덤 문제 조회 시나리오 RDD 책임 재배분"
  ```

---

## Task 2: 문제 제출 — RDD 책임 설계 및 코드 반영

**시나리오:** 사용자가 답을 제출하면 즉시 정답 여부와 해설이 반환된다. 이미 푼 문제면 이력을 업데이트한다.

**관여 객체:**
- `Answer` / `MultipleChoiceAnswer` / `ShortAnswer` (domain) — 답안 표현
- `Problem` / `MultipleChoiceProblem` / `ShortAnswerProblem` (domain) — 채점
- `UserProblemHistory` (domain) — 풀이 이력 생성/업데이트
- `SubmitAnswerService` (application) — 유스케이스 조율

**Files:**
- Modify: `domain/src/main/java/com/jms/assignment1/answer/Answer.java`
- Modify: `domain/src/main/java/com/jms/assignment1/answer/MultipleChoiceAnswer.java`
- Modify: `domain/src/main/java/com/jms/assignment1/answer/ShortAnswer.java`
- Modify: `domain/src/main/java/com/jms/assignment1/history/UserProblemHistory.java`
- Modify: `application/src/main/java/com/jms/assignment1/application/problem/SubmitAnswerService.java`
- Modify: `domain/src/test/java/com/jms/assignment1/answer/MultipleChoiceAnswerTest.java`
- Modify: `domain/src/test/java/com/jms/assignment1/answer/ShortAnswerTest.java`
- Modify: `domain/src/test/java/com/jms/assignment1/history/UserProblemHistoryTest.java`

**진행 방식 (대화 주도):**

- [ ] **Step 1: 시나리오 흐름 확인**

  ```
  사용자 요청 (problemId, userId, answerType, userAnswer)
    → 유저 존재 검증
    → 문제 조회
    → Answer 객체 생성
    → 채점 (evaluate)
    → 이력 생성 or 업데이트
    → 정답 여부 + 해설 반환
  ```

- [ ] **Step 2: 객체별 책임 대화 — Answer**

  사용자가 `Answer` 인터페이스와 구현체들의 책임을 제안한다.
  검토 포인트: `toText()`가 Answer의 책임인가? Answer는 무엇을 알아야 하는가?

- [ ] **Step 3: 객체별 책임 대화 — UserProblemHistory**

  사용자가 `UserProblemHistory`의 책임을 제안한다.
  검토 포인트: `update()`가 새 객체를 반환하는 것이 자연스러운가?

- [ ] **Step 4: 객체별 책임 대화 — SubmitAnswerService**

  사용자가 `SubmitAnswerService`의 책임을 제안한다.
  검토 포인트: `AnswerEvaluation`이라는 중간 객체가 필요한가?

- [ ] **Step 5: 합의된 책임을 코드에 반영**

- [ ] **Step 6: 테스트 수정 및 추가**

  ```bash
  ./gradlew :domain:test :application:test
  ```
  Expected: BUILD SUCCESSFUL

- [ ] **Step 7: 커밋**

  ```bash
  git add -p
  git commit -m "refactor: 문제 제출 시나리오 RDD 책임 재배분"
  ```

---

## Task 3: 풀이 이력 조회 — RDD 책임 설계 및 코드 반영

**시나리오:** 사용자가 이전에 풀었던 문제의 상세 정보(내 답, 정답, 해설, 정답률)를 조회한다.

**관여 객체:**
- `UserProblemHistory` (domain) — 이력 정보 보유
- `Problem` (domain) — 정답 정보 보유
- `GetProblemHistoryService` (application) — 유스케이스 조율

**Files:**
- Modify: `application/src/main/java/com/jms/assignment1/application/problem/GetProblemHistoryService.java`
- Modify: `application/src/main/java/com/jms/assignment1/application/problem/ProblemHistoryResult.java`

**진행 방식 (대화 주도):**

- [ ] **Step 1: 시나리오 흐름 확인**

  ```
  사용자 요청 (userId, problemId)
    → 유저 존재 검증
    → 문제 조회
    → 풀이 이력 조회
    → 정답률 계산
    → 상세 응답 반환
  ```

- [ ] **Step 2: 객체별 책임 대화 — GetProblemHistoryService**

  사용자가 `GetProblemHistoryService`의 책임을 제안한다.
  검토 포인트: 이 서비스가 알아야 하는 것이 너무 많지 않은가?

- [ ] **Step 3: 합의된 책임을 코드에 반영**

- [ ] **Step 4: 테스트 수정 및 추가**

  ```bash
  ./gradlew test
  ```
  Expected: BUILD SUCCESSFUL

- [ ] **Step 5: 커밋**

  ```bash
  git add -p
  git commit -m "refactor: 풀이 이력 조회 시나리오 RDD 책임 재배분"
  ```

---

## Task 4: api 레이어 정합성 검토

Task 1-3에서 도메인/애플리케이션 레이어 책임이 바뀌면 api 레이어 DTO, 컨트롤러도 영향을 받을 수 있다.

**Files:**
- Modify: `api/src/main/java/com/jms/assignment1/api/problem/ProblemController.java`
- Modify: `api/src/main/java/com/jms/assignment1/api/problem/dto/*.java` (필요 시)

- [ ] **Step 1: api 레이어 영향 확인**

  Task 1-3에서 변경된 Result 객체, 메서드 시그니처를 api 레이어가 올바르게 사용하는지 확인한다.

- [ ] **Step 2: 필요한 수정 반영**

- [ ] **Step 3: 전체 테스트**

  ```bash
  ./gradlew test
  ```
  Expected: BUILD SUCCESSFUL

- [ ] **Step 4: 최종 커밋**

  ```bash
  git add -p
  git commit -m "refactor: api 레이어 RDD 리팩터링 반영"
  ```
