# CLAUDE.md

## 프로젝트 개요

DDD 헥사고날 멀티모듈 학습 플랫폼 백엔드.
모듈 구조: `domain` → `application` → `infrastructure` ← `api`

## 코딩 규칙

### 변수명
- 축약 금지. 무엇을 나타내는지 명확히 알 수 있게 작성
- 나쁜 예: `mca`, `sa`, `e`, `p`
- 좋은 예: `multipleChoiceAnswer`, `shortAnswer`, `exception`, `problem`

### 연산자
- 삼항 연산자 사용 금지. if/else로 작성

### 주석
- 주석 작성 금지. 코드 자체로 의도를 표현

### 도메인 모듈
- Spring, JPA 어노테이션 절대 금지
- 순수 Java만 사용

## 아키텍처 결정사항

| 항목 | 결정 |
|---|---|
| Problem 타입 | 상속 구조 (`Problem` → `MultipleChoiceProblem` / `ShortAnswerProblem`) |
| Answer 타입 | 상속 구조 (`Answer` → `MultipleChoiceAnswer` / `ShortAnswer`) |
| Problem.evaluate | 오버로딩으로 타입 안전성 확보. 제네릭 사용 안 함 |
| JPA 상속 | JOINED 전략 |
| 건너뛰기 추적 | `UserChapterSkip` 엔티티 (userId+chapterId 기준, surrogate id 포함) |
| 주관식 채점 | 앞뒤 공백 무시(strip), 중간 공백은 오답 처리 |
| AnswerStatus | `CORRECT`, `PARTIAL`, `WRONG` |

## 테스트 규칙

- 테스트 메서드명은 한글로 시나리오를 명확히 작성
- 순서: 생성자 검증 테스트 → 동작 테스트
- 각 클래스의 테스트는 해당 클래스 전용 파일에 작성
  - 예: `ShortAnswer` 생성자 검증 → `ShortAnswerTest.java`
- 경계값과 분기점 위주로 테스트

## 작업 완료 기준

기능 구현이 완료되면 반드시 테스트를 실행한다.
- domain: `./gradlew :domain:test`
- application: `./gradlew :application:test`
- 전체: `./gradlew test`

테스트가 실패하면 커밋하지 않고 원인을 분석하여 해결 방법을 출력한 뒤 수정한다.
