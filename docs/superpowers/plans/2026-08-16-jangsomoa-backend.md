# jangsomoa Backend Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** DDD 헥사고날 멀티모듈 구조로 학습 플랫폼의 문제 풀이 및 이력 조회 API 구축

**Architecture:** domain → application → infrastructure ← api 의존 방향. domain은 순수 Java (Spring/JPA 없음). infrastructure가 domain Repository 인터페이스를 구현 (의존성 역전).

**Tech Stack:** Java 21, Spring Boot 4.1.0, Spring Data JPA, H2 (test), MySQL (prod), Lombok, JUnit 5, Mockito, AssertJ

## Global Constraints

- Java 21 (pattern matching instanceof, records 적극 활용)
- Spring Boot 4.1.0 (BOM으로 모든 의존성 버전 관리)
- 기본 패키지: `com.jms.jangsomoa`
- domain 모듈에 Spring/JPA 어노테이션 절대 금지
- Lombok `@Getter @RequiredArgsConstructor` 사용 (도메인 객체는 불변)
- 테스트: domain/application은 순수 JUnit5+Mockito, infrastructure는 @DataJpaTest+H2, api는 @SpringBootTest+MockMvc

---

### Task 1: Multi-module Gradle Setup

**Files:**
- Modify: `build.gradle` (root)
- Modify: `settings.gradle`
- Create: `domain/build.gradle`
- Create: `application/build.gradle`
- Create: `infrastructure/build.gradle`
- Create: `api/build.gradle`
- Move: `src/` → `api/src/`
- Move: `src/main/resources/application.properties` → `api/src/main/resources/application.yml`

**Interfaces:**
- Produces: 빌드 가능한 4-모듈 Gradle 프로젝트

- [ ] **Step 1: root build.gradle 교체**

```groovy
// build.gradle (root)
plugins {
    id 'java'
    id 'org.springframework.boot' version '4.1.0' apply false
    id 'io.spring.dependency-management' version '1.1.7'
}

subprojects {
    apply plugin: 'java'
    apply plugin: 'io.spring.dependency-management'

    group = 'com.jms'
    version = '0.0.1-SNAPSHOT'

    java {
        toolchain {
            languageVersion = JavaLanguageVersion.of(21)
        }
    }

    repositories {
        mavenCentral()
    }

    dependencyManagement {
        imports {
            mavenBom "org.springframework.boot:spring-boot-dependencies:4.1.0"
        }
    }
}
```

- [ ] **Step 2: settings.gradle 교체**

```groovy
rootProject.name = 'jangsomoa'
include 'domain', 'application', 'infrastructure', 'api'
```

- [ ] **Step 3: 각 모듈 build.gradle 생성**

```groovy
// domain/build.gradle
dependencies {
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.junit.jupiter:junit-jupiter'
    testImplementation 'org.assertj:assertj-core'
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
tasks.named('test') { useJUnitPlatform() }
```

```groovy
// application/build.gradle
dependencies {
    implementation project(':domain')
    implementation 'org.springframework:spring-context'
    implementation 'org.springframework:spring-tx'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    testImplementation 'org.junit.jupiter:junit-jupiter'
    testImplementation 'org.assertj:assertj-core'
    testImplementation 'org.mockito:mockito-core'
    testImplementation 'org.mockito:mockito-junit-jupiter'
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
tasks.named('test') { useJUnitPlatform() }
```

```groovy
// infrastructure/build.gradle
dependencies {
    implementation project(':domain')
    implementation project(':application')
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'com.fasterxml.jackson.core:jackson-databind'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    runtimeOnly 'com.h2database:h2'
    runtimeOnly 'com.mysql:mysql-connector-j'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
tasks.named('test') { useJUnitPlatform() }
bootJar { enabled = false }
jar { enabled = true }
```

```groovy
// api/build.gradle
apply plugin: 'org.springframework.boot'

dependencies {
    implementation project(':domain')
    implementation project(':application')
    implementation project(':infrastructure')
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-validation'
    implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    compileOnly 'org.projectlombok:lombok'
    annotationProcessor 'org.projectlombok:lombok'
    developmentOnly 'org.springframework.boot:spring-boot-devtools'
    runtimeOnly 'com.h2database:h2'
    runtimeOnly 'com.mysql:mysql-connector-j'
    testImplementation 'org.springframework.boot:spring-boot-starter-test'
    testCompileOnly 'org.projectlombok:lombok'
    testAnnotationProcessor 'org.projectlombok:lombok'
    testRuntimeOnly 'org.junit.platform:junit-platform-launcher'
}
tasks.named('test') { useJUnitPlatform() }
```

- [ ] **Step 4: 모듈 디렉토리 구조 생성**

```bash
mkdir -p domain/src/main/java/com/jms/jangsomoa/domain
mkdir -p domain/src/test/java/com/jms/jangsomoa/domain
mkdir -p application/src/main/java/com/jms/jangsomoa/application
mkdir -p application/src/test/java/com/jms/jangsomoa/application
mkdir -p infrastructure/src/main/java/com/jms/jangsomoa/infrastructure
mkdir -p infrastructure/src/test/java/com/jms/jangsomoa/infrastructure
mkdir -p api/src/main/java/com/jms/jangsomoa
mkdir -p api/src/main/resources
mkdir -p api/src/test/java/com/jms/jangsomoa/api
mkdir -p api/src/test/resources
```

- [ ] **Step 5: 기존 파일 이동**

```bash
# Application 클래스 이동
mv src/main/java/com/jms/jangsomoa/JangsomoaApplication.java \
   api/src/main/java/com/jms/jangsomoa/JangsomoaApplication.java

# 테스트 이동
mv src/test/java/com/jms/jangsomoa/JangsomoaApplicationTests.java \
   api/src/test/java/com/jms/jangsomoa/JangsomoaApplicationTests.java

# 설정 이동 (application.properties → application.yml로 변환 예정)
rm src/main/resources/application.properties

# 빈 src 디렉토리 제거
rm -rf src/
```

- [ ] **Step 6: api/src/main/resources/application.yml 생성**

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/jangsomoa?serverTimezone=UTC&characterEncoding=UTF-8
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:root}
    driver-class-name: com.mysql.cj.jdbc.Driver
  jpa:
    hibernate:
      ddl-auto: create
    show-sql: false
    open-in-view: false
  sql:
    init:
      mode: always
      data-locations: classpath:data.sql
```

- [ ] **Step 7: 빌드 확인**

```bash
./gradlew build -x test
```

Expected: `BUILD SUCCESSFUL`

- [ ] **Step 8: Commit**

```bash
git add build.gradle settings.gradle domain/build.gradle application/build.gradle \
        infrastructure/build.gradle api/build.gradle \
        api/src/main/java/com/jms/jangsomoa/JangsomoaApplication.java \
        api/src/main/resources/application.yml \
        api/src/test/java/com/jms/jangsomoa/JangsomoaApplicationTests.java
git commit -m "build: convert to 4-module Gradle project (domain/application/infrastructure/api)"
```

---

### Task 2: Domain — Problem 계층 + Answer 계층 + CorrectRateCalculator

**Files:**
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/answer/AnswerStatus.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/answer/AnswerType.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/answer/UserAnswer.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/answer/MultipleChoiceAnswer.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/answer/ShortAnswer.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/problem/Problem.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/problem/MultipleChoiceProblem.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/problem/ShortAnswerProblem.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/service/CorrectRateCalculator.java`
- Test: `domain/src/test/java/com/jms/jangsomoa/domain/problem/MultipleChoiceProblemTest.java`
- Test: `domain/src/test/java/com/jms/jangsomoa/domain/problem/ShortAnswerProblemTest.java`
- Test: `domain/src/test/java/com/jms/jangsomoa/domain/service/CorrectRateCalculatorTest.java`

**Interfaces:**
- Produces:
  - `AnswerStatus` enum: `CORRECT, PARTIAL, INCORRECT`
  - `AnswerType` enum: `MULTIPLE_CHOICE, SHORT_ANSWER`
  - `UserAnswer` abstract class with `getType(): AnswerType`
  - `MultipleChoiceAnswer(List<Integer> selectedChoices)`
  - `ShortAnswer(String text)`
  - `Problem` abstract class: `getId(), getChapterId(), getContent(), getExplanation()`
  - `MultipleChoiceProblem(Long id, Long chapterId, String content, String explanation, List<String> choices, List<Integer> correctAnswers)` + `evaluate(MultipleChoiceAnswer): AnswerStatus`
  - `ShortAnswerProblem(Long id, Long chapterId, String content, String explanation, String correctAnswer)` + `evaluate(ShortAnswer): AnswerStatus`
  - `CorrectRateCalculator.calculate(List<AnswerStatus>): Integer` (null if < 30)

- [ ] **Step 1: 테스트 먼저 작성**

```java
// domain/src/test/java/com/jms/jangsomoa/domain/problem/MultipleChoiceProblemTest.java
package com.jms.jangsomoa.domain.problem;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import com.jms.jangsomoa.domain.answer.MultipleChoiceAnswer;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class MultipleChoiceProblemTest {
    private final MultipleChoiceProblem problem = new MultipleChoiceProblem(
            1L, 1L, "문제", "해설",
            List.of("가", "나", "다", "라", "마"), List.of(1, 2));

    @Test
    void 정답을_모두_선택하면_CORRECT() {
        assertThat(problem.evaluate(new MultipleChoiceAnswer(List.of(1, 2))))
                .isEqualTo(AnswerStatus.CORRECT);
    }

    @Test
    void 정답_일부와_오답을_포함하면_PARTIAL() {
        assertThat(problem.evaluate(new MultipleChoiceAnswer(List.of(1, 3))))
                .isEqualTo(AnswerStatus.PARTIAL);
    }

    @Test
    void 정답만_부분적으로_선택해도_PARTIAL() {
        assertThat(problem.evaluate(new MultipleChoiceAnswer(List.of(1))))
                .isEqualTo(AnswerStatus.PARTIAL);
    }

    @Test
    void 정답을_하나도_선택하지_않으면_INCORRECT() {
        assertThat(problem.evaluate(new MultipleChoiceAnswer(List.of(3, 4))))
                .isEqualTo(AnswerStatus.INCORRECT);
    }
}
```

```java
// domain/src/test/java/com/jms/jangsomoa/domain/problem/ShortAnswerProblemTest.java
package com.jms.jangsomoa.domain.problem;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import com.jms.jangsomoa.domain.answer.ShortAnswer;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class ShortAnswerProblemTest {
    private final ShortAnswerProblem problem = new ShortAnswerProblem(
            1L, 1L, "대한민국의 수도는?", "해설", "서울");

    @Test
    void 정확한_답을_제출하면_CORRECT() {
        assertThat(problem.evaluate(new ShortAnswer("서울"))).isEqualTo(AnswerStatus.CORRECT);
    }

    @Test
    void 앞뒤_공백은_무시한다() {
        assertThat(problem.evaluate(new ShortAnswer("  서울  "))).isEqualTo(AnswerStatus.CORRECT);
    }

    @Test
    void 틀린_답을_제출하면_INCORRECT() {
        assertThat(problem.evaluate(new ShortAnswer("부산"))).isEqualTo(AnswerStatus.INCORRECT);
    }
}
```

```java
// domain/src/test/java/com/jms/jangsomoa/domain/service/CorrectRateCalculatorTest.java
package com.jms.jangsomoa.domain.service;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;

class CorrectRateCalculatorTest {

    @Test
    void 제출자가_30명_미만이면_null() {
        List<AnswerStatus> statuses = List.of(AnswerStatus.CORRECT, AnswerStatus.INCORRECT);
        assertThat(CorrectRateCalculator.calculate(statuses)).isNull();
    }

    @Test
    void 제출자가_30명_이상이면_정답률_반환() {
        List<AnswerStatus> statuses = new ArrayList<>();
        for (int i = 0; i < 20; i++) statuses.add(AnswerStatus.CORRECT);
        for (int i = 0; i < 10; i++) statuses.add(AnswerStatus.INCORRECT);
        // 20/30 = 66.666...% → round → 67
        assertThat(CorrectRateCalculator.calculate(statuses)).isEqualTo(67);
    }

    @Test
    void PARTIAL은_오답으로_간주() {
        List<AnswerStatus> statuses = new ArrayList<>();
        for (int i = 0; i < 30; i++) statuses.add(AnswerStatus.CORRECT);
        for (int i = 0; i < 10; i++) statuses.add(AnswerStatus.PARTIAL);
        // 30/40 = 75%
        assertThat(CorrectRateCalculator.calculate(statuses)).isEqualTo(75);
    }

    @Test
    void 정확히_30명이면_계산한다() {
        List<AnswerStatus> statuses = new ArrayList<>();
        for (int i = 0; i < 30; i++) statuses.add(AnswerStatus.CORRECT);
        assertThat(CorrectRateCalculator.calculate(statuses)).isEqualTo(100);
    }
}
```

- [ ] **Step 2: 테스트 실행 — FAIL 확인**

```bash
./gradlew :domain:test
```

Expected: FAIL (클래스 없음)

- [ ] **Step 3: 구현**

```java
// domain/src/main/java/com/jms/jangsomoa/domain/answer/AnswerStatus.java
package com.jms.jangsomoa.domain.answer;
public enum AnswerStatus { CORRECT, PARTIAL, INCORRECT }
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/answer/AnswerType.java
package com.jms.jangsomoa.domain.answer;
public enum AnswerType { MULTIPLE_CHOICE, SHORT_ANSWER }
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/answer/UserAnswer.java
package com.jms.jangsomoa.domain.answer;
public abstract class UserAnswer {
    public abstract AnswerType getType();
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/answer/MultipleChoiceAnswer.java
package com.jms.jangsomoa.domain.answer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class MultipleChoiceAnswer extends UserAnswer {
    private final List<Integer> selectedChoices;

    @Override
    public AnswerType getType() { return AnswerType.MULTIPLE_CHOICE; }
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/answer/ShortAnswer.java
package com.jms.jangsomoa.domain.answer;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ShortAnswer extends UserAnswer {
    private final String text;

    @Override
    public AnswerType getType() { return AnswerType.SHORT_ANSWER; }
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/problem/Problem.java
package com.jms.jangsomoa.domain.problem;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public abstract class Problem {
    private final Long id;
    private final Long chapterId;
    private final String content;
    private final String explanation;
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/problem/MultipleChoiceProblem.java
package com.jms.jangsomoa.domain.problem;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import com.jms.jangsomoa.domain.answer.MultipleChoiceAnswer;
import lombok.Getter;
import java.util.HashSet;
import java.util.List;

@Getter
public class MultipleChoiceProblem extends Problem {
    private final List<String> choices;
    private final List<Integer> correctAnswers;

    public MultipleChoiceProblem(Long id, Long chapterId, String content, String explanation,
                                  List<String> choices, List<Integer> correctAnswers) {
        super(id, chapterId, content, explanation);
        this.choices = choices;
        this.correctAnswers = correctAnswers;
    }

    public AnswerStatus evaluate(MultipleChoiceAnswer answer) {
        List<Integer> selected = answer.getSelectedChoices();
        if (new HashSet<>(selected).equals(new HashSet<>(correctAnswers))) {
            return AnswerStatus.CORRECT;
        }
        boolean hasAnyCorrect = selected.stream().anyMatch(correctAnswers::contains);
        return hasAnyCorrect ? AnswerStatus.PARTIAL : AnswerStatus.INCORRECT;
    }
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/problem/ShortAnswerProblem.java
package com.jms.jangsomoa.domain.problem;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import com.jms.jangsomoa.domain.answer.ShortAnswer;
import lombok.Getter;

@Getter
public class ShortAnswerProblem extends Problem {
    private final String correctAnswer;

    public ShortAnswerProblem(Long id, Long chapterId, String content, String explanation,
                               String correctAnswer) {
        super(id, chapterId, content, explanation);
        this.correctAnswer = correctAnswer;
    }

    public AnswerStatus evaluate(ShortAnswer answer) {
        return correctAnswer.equalsIgnoreCase(answer.getText().trim())
                ? AnswerStatus.CORRECT : AnswerStatus.INCORRECT;
    }
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/service/CorrectRateCalculator.java
package com.jms.jangsomoa.domain.service;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import java.util.List;

public class CorrectRateCalculator {
    private static final int MIN_SAMPLE_SIZE = 30;

    public static Integer calculate(List<AnswerStatus> statuses) {
        if (statuses.size() < MIN_SAMPLE_SIZE) return null;
        long correct = statuses.stream().filter(s -> s == AnswerStatus.CORRECT).count();
        return (int) Math.round((double) correct / statuses.size() * 100);
    }
}
```

- [ ] **Step 4: 테스트 PASS 확인**

```bash
./gradlew :domain:test
```

Expected: BUILD SUCCESSFUL, 모든 테스트 PASS

- [ ] **Step 5: Commit**

```bash
git add domain/
git commit -m "feat: add domain problem/answer hierarchy and CorrectRateCalculator"
```

---

### Task 3: Domain — 나머지 엔티티 + Repository 인터페이스 + 예외

**Files:**
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/user/User.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/chapter/Chapter.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/chapter/UserChapterSkip.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/history/UserProblemHistory.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/repository/ProblemRepository.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/repository/UserProblemHistoryRepository.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/repository/ChapterRepository.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/repository/UserChapterSkipRepository.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/repository/UserRepository.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/exception/DomainException.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/exception/ProblemNotFoundException.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/exception/UserNotFoundException.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/exception/ChapterNotFoundException.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/exception/NoAvailableProblemException.java`
- Create: `domain/src/main/java/com/jms/jangsomoa/domain/exception/ProblemHistoryNotFoundException.java`

**Interfaces:**
- Consumes: Task 2의 `AnswerStatus`, `UserAnswer`
- Produces:
  - `User(Long id, String name)`
  - `Chapter(Long id, String name)`
  - `UserChapterSkip(Long id, Long userId, Long chapterId, Long skippedProblemId)`
  - `UserProblemHistory(Long id, Long userId, Long problemId, AnswerStatus, UserAnswer)` + `create()` 정적 팩토리
  - Repository 인터페이스 5개 (아래 명세 참조)
  - 예외 클래스 5개

- [ ] **Step 1: 엔티티 클래스 작성**

```java
// domain/src/main/java/com/jms/jangsomoa/domain/user/User.java
package com.jms.jangsomoa.domain.user;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class User {
    private final Long id;
    private final String name;
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/chapter/Chapter.java
package com.jms.jangsomoa.domain.chapter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class Chapter {
    private final Long id;
    private final String name;
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/chapter/UserChapterSkip.java
package com.jms.jangsomoa.domain.chapter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserChapterSkip {
    private final Long id;
    private final Long userId;
    private final Long chapterId;
    private final Long skippedProblemId;
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/history/UserProblemHistory.java
package com.jms.jangsomoa.domain.history;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import com.jms.jangsomoa.domain.answer.UserAnswer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class UserProblemHistory {
    private final Long id;
    private final Long userId;
    private final Long problemId;
    private final AnswerStatus answerStatus;
    private final UserAnswer userAnswer;

    public static UserProblemHistory create(Long userId, Long problemId,
                                             AnswerStatus answerStatus, UserAnswer userAnswer) {
        return new UserProblemHistory(null, userId, problemId, answerStatus, userAnswer);
    }
}
```

- [ ] **Step 2: Repository 인터페이스 작성**

```java
// domain/src/main/java/com/jms/jangsomoa/domain/repository/ProblemRepository.java
package com.jms.jangsomoa.domain.repository;

import com.jms.jangsomoa.domain.problem.Problem;
import java.util.List;
import java.util.Optional;

public interface ProblemRepository {
    Optional<Problem> findById(Long id);
    List<Problem> findByChapterId(Long chapterId);
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/repository/UserProblemHistoryRepository.java
package com.jms.jangsomoa.domain.repository;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import com.jms.jangsomoa.domain.history.UserProblemHistory;
import java.util.List;
import java.util.Optional;

public interface UserProblemHistoryRepository {
    List<Long> findSolvedProblemIdsByUserIdAndChapterId(Long userId, Long chapterId);
    List<AnswerStatus> findAnswerStatusesByProblemId(Long problemId);
    void save(UserProblemHistory history);
    Optional<UserProblemHistory> findByUserIdAndProblemId(Long userId, Long problemId);
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/repository/ChapterRepository.java
package com.jms.jangsomoa.domain.repository;

import com.jms.jangsomoa.domain.chapter.Chapter;
import java.util.Optional;

public interface ChapterRepository {
    Optional<Chapter> findById(Long id);
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/repository/UserChapterSkipRepository.java
package com.jms.jangsomoa.domain.repository;

import java.util.Optional;

public interface UserChapterSkipRepository {
    Optional<Long> findSkippedProblemId(Long userId, Long chapterId);
    void upsert(Long userId, Long chapterId, Long skippedProblemId);
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/repository/UserRepository.java
package com.jms.jangsomoa.domain.repository;

import com.jms.jangsomoa.domain.user.User;
import java.util.Optional;

public interface UserRepository {
    Optional<User> findById(Long id);
}
```

- [ ] **Step 3: 예외 클래스 작성**

```java
// domain/src/main/java/com/jms/jangsomoa/domain/exception/DomainException.java
package com.jms.jangsomoa.domain.exception;

public abstract class DomainException extends RuntimeException {
    protected DomainException(String message) { super(message); }
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/exception/ProblemNotFoundException.java
package com.jms.jangsomoa.domain.exception;

public class ProblemNotFoundException extends DomainException {
    public ProblemNotFoundException(Long id) { super("Problem not found: " + id); }
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/exception/UserNotFoundException.java
package com.jms.jangsomoa.domain.exception;

public class UserNotFoundException extends DomainException {
    public UserNotFoundException(Long id) { super("User not found: " + id); }
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/exception/ChapterNotFoundException.java
package com.jms.jangsomoa.domain.exception;

public class ChapterNotFoundException extends DomainException {
    public ChapterNotFoundException(Long id) { super("Chapter not found: " + id); }
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/exception/NoAvailableProblemException.java
package com.jms.jangsomoa.domain.exception;

public class NoAvailableProblemException extends DomainException {
    public NoAvailableProblemException() { super("No available problem in this chapter"); }
}
```

```java
// domain/src/main/java/com/jms/jangsomoa/domain/exception/ProblemHistoryNotFoundException.java
package com.jms.jangsomoa.domain.exception;

public class ProblemHistoryNotFoundException extends DomainException {
    public ProblemHistoryNotFoundException(Long userId, Long problemId) {
        super("History not found for userId=" + userId + ", problemId=" + problemId);
    }
}
```

- [ ] **Step 4: 컴파일 확인**

```bash
./gradlew :domain:compileJava
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add domain/
git commit -m "feat: add domain entities, repository interfaces, and exceptions"
```

---

### Task 4: Application — GetRandomProblemUseCase

**Files:**
- Create: `application/src/main/java/com/jms/jangsomoa/application/problem/GetRandomProblemCommand.java`
- Create: `application/src/main/java/com/jms/jangsomoa/application/problem/GetRandomProblemResult.java`
- Create: `application/src/main/java/com/jms/jangsomoa/application/problem/GetRandomProblemUseCase.java`
- Test: `application/src/test/java/com/jms/jangsomoa/application/problem/GetRandomProblemUseCaseTest.java`

**Interfaces:**
- Consumes: `ChapterRepository`, `ProblemRepository`, `UserProblemHistoryRepository`, `UserChapterSkipRepository`, `UserRepository`, `CorrectRateCalculator`, 도메인 예외 5종
- Produces: `GetRandomProblemUseCase.execute(GetRandomProblemCommand): GetRandomProblemResult`
  - `GetRandomProblemCommand(Long userId, Long chapterId)`
  - `GetRandomProblemResult(Long problemId, String content, List<String> choices, Integer answerCorrectRate)`

- [ ] **Step 1: 테스트 작성**

```java
// application/src/test/java/com/jms/jangsomoa/application/problem/GetRandomProblemUseCaseTest.java
package com.jms.jangsomoa.application.problem;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import com.jms.jangsomoa.domain.chapter.Chapter;
import com.jms.jangsomoa.domain.exception.ChapterNotFoundException;
import com.jms.jangsomoa.domain.exception.NoAvailableProblemException;
import com.jms.jangsomoa.domain.exception.UserNotFoundException;
import com.jms.jangsomoa.domain.problem.MultipleChoiceProblem;
import com.jms.jangsomoa.domain.problem.Problem;
import com.jms.jangsomoa.domain.repository.*;
import com.jms.jangsomoa.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetRandomProblemUseCaseTest {
    @Mock private UserRepository userRepository;
    @Mock private ChapterRepository chapterRepository;
    @Mock private ProblemRepository problemRepository;
    @Mock private UserProblemHistoryRepository historyRepository;
    @Mock private UserChapterSkipRepository skipRepository;

    @InjectMocks private GetRandomProblemUseCase useCase;

    private final User user = new User(1L, "테스터");
    private final Chapter chapter = new Chapter(1L, "1단원");
    private final MultipleChoiceProblem problem1 = new MultipleChoiceProblem(
            1L, 1L, "문제1", "해설1", List.of("가", "나", "다", "라", "마"), List.of(1));
    private final MultipleChoiceProblem problem2 = new MultipleChoiceProblem(
            2L, 1L, "문제2", "해설2", List.of("a", "b", "c", "d", "e"), List.of(2));

    @Test
    void 미풀이_문제_중_하나를_반환한다() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(problemRepository.findByChapterId(1L)).thenReturn(List.of(problem1, problem2));
        when(historyRepository.findSolvedProblemIdsByUserIdAndChapterId(1L, 1L))
                .thenReturn(List.of(1L));
        when(skipRepository.findSkippedProblemId(1L, 1L)).thenReturn(Optional.empty());
        when(historyRepository.findAnswerStatusesByProblemId(2L)).thenReturn(List.of());

        GetRandomProblemResult result = useCase.execute(new GetRandomProblemCommand(1L, 1L));

        assertThat(result.problemId()).isEqualTo(2L);
        assertThat(result.answerCorrectRate()).isNull();
    }

    @Test
    void 건너뛴_문제는_후보에서_제외된다() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(problemRepository.findByChapterId(1L)).thenReturn(List.of(problem1, problem2));
        when(historyRepository.findSolvedProblemIdsByUserIdAndChapterId(1L, 1L))
                .thenReturn(List.of());
        when(skipRepository.findSkippedProblemId(1L, 1L)).thenReturn(Optional.of(1L));
        when(historyRepository.findAnswerStatusesByProblemId(2L)).thenReturn(List.of());

        GetRandomProblemResult result = useCase.execute(new GetRandomProblemCommand(1L, 1L));

        assertThat(result.problemId()).isEqualTo(2L);
    }

    @Test
    void 풀_수_있는_문제가_없으면_NoAvailableProblemException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(problemRepository.findByChapterId(1L)).thenReturn(List.of(problem1));
        when(historyRepository.findSolvedProblemIdsByUserIdAndChapterId(1L, 1L))
                .thenReturn(List.of(1L));
        when(skipRepository.findSkippedProblemId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new GetRandomProblemCommand(1L, 1L)))
                .isInstanceOf(NoAvailableProblemException.class);
    }

    @Test
    void 정답률_30명_이상이면_계산값_반환() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(chapterRepository.findById(1L)).thenReturn(Optional.of(chapter));
        when(problemRepository.findByChapterId(1L)).thenReturn(List.of(problem2));
        when(historyRepository.findSolvedProblemIdsByUserIdAndChapterId(1L, 1L))
                .thenReturn(List.of());
        when(skipRepository.findSkippedProblemId(1L, 1L)).thenReturn(Optional.empty());
        List<AnswerStatus> statuses = new ArrayList<>();
        for (int i = 0; i < 20; i++) statuses.add(AnswerStatus.CORRECT);
        for (int i = 0; i < 10; i++) statuses.add(AnswerStatus.INCORRECT);
        when(historyRepository.findAnswerStatusesByProblemId(2L)).thenReturn(statuses);

        GetRandomProblemResult result = useCase.execute(new GetRandomProblemCommand(1L, 1L));

        assertThat(result.answerCorrectRate()).isEqualTo(67);
    }

    @Test
    void 존재하지_않는_사용자면_UserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new GetRandomProblemCommand(99L, 1L)))
                .isInstanceOf(UserNotFoundException.class);
    }

    @Test
    void 존재하지_않는_단원이면_ChapterNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(chapterRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new GetRandomProblemCommand(1L, 99L)))
                .isInstanceOf(ChapterNotFoundException.class);
    }
}
```

- [ ] **Step 2: 테스트 실행 — FAIL 확인**

```bash
./gradlew :application:test
```

Expected: FAIL (클래스 없음)

- [ ] **Step 3: Command/Result 레코드 작성**

```java
// application/src/main/java/com/jms/jangsomoa/application/problem/GetRandomProblemCommand.java
package com.jms.jangsomoa.application.problem;

public record GetRandomProblemCommand(Long userId, Long chapterId) {}
```

```java
// application/src/main/java/com/jms/jangsomoa/application/problem/GetRandomProblemResult.java
package com.jms.jangsomoa.application.problem;

import java.util.List;

public record GetRandomProblemResult(
        Long problemId,
        String content,
        List<String> choices,       // 객관식만. 주관식은 null
        Integer answerCorrectRate   // 30명 미만이면 null
) {}
```

- [ ] **Step 4: UseCase 구현**

```java
// application/src/main/java/com/jms/jangsomoa/application/problem/GetRandomProblemUseCase.java
package com.jms.jangsomoa.application.problem;

import com.jms.jangsomoa.domain.exception.*;
import com.jms.jangsomoa.domain.problem.MultipleChoiceProblem;
import com.jms.jangsomoa.domain.problem.Problem;
import com.jms.jangsomoa.domain.repository.*;
import com.jms.jangsomoa.domain.service.CorrectRateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class GetRandomProblemUseCase {
    private final UserRepository userRepository;
    private final ChapterRepository chapterRepository;
    private final ProblemRepository problemRepository;
    private final UserProblemHistoryRepository historyRepository;
    private final UserChapterSkipRepository skipRepository;

    @Transactional
    public GetRandomProblemResult execute(GetRandomProblemCommand command) {
        userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));
        chapterRepository.findById(command.chapterId())
                .orElseThrow(() -> new ChapterNotFoundException(command.chapterId()));

        List<Problem> allProblems = problemRepository.findByChapterId(command.chapterId());
        List<Long> solvedIds = historyRepository.findSolvedProblemIdsByUserIdAndChapterId(
                command.userId(), command.chapterId());
        Optional<Long> skippedId = skipRepository.findSkippedProblemId(
                command.userId(), command.chapterId());

        List<Problem> candidates = allProblems.stream()
                .filter(p -> !solvedIds.contains(p.getId()))
                .filter(p -> skippedId.map(id -> !id.equals(p.getId())).orElse(true))
                .toList();

        if (candidates.isEmpty()) throw new NoAvailableProblemException();

        Problem selected = candidates.get(new Random().nextInt(candidates.size()));
        skipRepository.upsert(command.userId(), command.chapterId(), selected.getId());

        Integer correctRate = CorrectRateCalculator.calculate(
                historyRepository.findAnswerStatusesByProblemId(selected.getId()));

        List<String> choices = selected instanceof MultipleChoiceProblem mcp
                ? mcp.getChoices() : null;

        return new GetRandomProblemResult(
                selected.getId(), selected.getContent(), choices, correctRate);
    }
}
```

- [ ] **Step 5: 테스트 PASS 확인**

```bash
./gradlew :application:test --tests "*.GetRandomProblemUseCaseTest"
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 6: Commit**

```bash
git add application/
git commit -m "feat: add GetRandomProblemUseCase"
```

---

### Task 5: Application — SubmitAnswerUseCase

**Files:**
- Create: `application/src/main/java/com/jms/jangsomoa/application/problem/SubmitAnswerCommand.java`
- Create: `application/src/main/java/com/jms/jangsomoa/application/problem/SubmitAnswerResult.java`
- Create: `application/src/main/java/com/jms/jangsomoa/application/problem/SubmitAnswerUseCase.java`
- Test: `application/src/test/java/com/jms/jangsomoa/application/problem/SubmitAnswerUseCaseTest.java`

**Interfaces:**
- Consumes: Task 3의 모든 Repository 인터페이스, Task 2의 Problem 계층
- Produces: `SubmitAnswerUseCase.execute(SubmitAnswerCommand): SubmitAnswerResult`
  - `SubmitAnswerCommand(Long userId, Long problemId, AnswerType answerType, List<Integer> selectedChoices, String text)`
  - `SubmitAnswerResult(AnswerStatus answerStatus, String explanation, List<Integer> correctChoices, String correctText)`

- [ ] **Step 1: 테스트 작성**

```java
// application/src/test/java/com/jms/jangsomoa/application/problem/SubmitAnswerUseCaseTest.java
package com.jms.jangsomoa.application.problem;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import com.jms.jangsomoa.domain.answer.AnswerType;
import com.jms.jangsomoa.domain.exception.ProblemNotFoundException;
import com.jms.jangsomoa.domain.exception.UserNotFoundException;
import com.jms.jangsomoa.domain.history.UserProblemHistory;
import com.jms.jangsomoa.domain.problem.MultipleChoiceProblem;
import com.jms.jangsomoa.domain.problem.ShortAnswerProblem;
import com.jms.jangsomoa.domain.repository.*;
import com.jms.jangsomoa.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerUseCaseTest {
    @Mock private UserRepository userRepository;
    @Mock private ProblemRepository problemRepository;
    @Mock private UserProblemHistoryRepository historyRepository;

    @InjectMocks private SubmitAnswerUseCase useCase;

    private final User user = new User(1L, "테스터");
    private final MultipleChoiceProblem mcProblem = new MultipleChoiceProblem(
            1L, 1L, "문제", "해설입니다",
            List.of("가", "나", "다", "라", "마"), List.of(1, 2));
    private final ShortAnswerProblem saProblem = new ShortAnswerProblem(
            2L, 1L, "수도는?", "서울입니다", "서울");

    @Test
    void 객관식_정답_제출시_CORRECT_반환() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(problemRepository.findById(1L)).thenReturn(Optional.of(mcProblem));

        SubmitAnswerResult result = useCase.execute(
                new SubmitAnswerCommand(1L, 1L, AnswerType.MULTIPLE_CHOICE, List.of(1, 2), null));

        assertThat(result.answerStatus()).isEqualTo(AnswerStatus.CORRECT);
        assertThat(result.explanation()).isEqualTo("해설입니다");
        assertThat(result.correctChoices()).containsExactly(1, 2);
        assertThat(result.correctText()).isNull();
    }

    @Test
    void 객관식_부분정답_제출시_PARTIAL_반환() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(problemRepository.findById(1L)).thenReturn(Optional.of(mcProblem));

        SubmitAnswerResult result = useCase.execute(
                new SubmitAnswerCommand(1L, 1L, AnswerType.MULTIPLE_CHOICE, List.of(1, 3), null));

        assertThat(result.answerStatus()).isEqualTo(AnswerStatus.PARTIAL);
    }

    @Test
    void 주관식_정답_제출시_CORRECT_반환() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(problemRepository.findById(2L)).thenReturn(Optional.of(saProblem));

        SubmitAnswerResult result = useCase.execute(
                new SubmitAnswerCommand(1L, 2L, AnswerType.SHORT_ANSWER, null, "서울"));

        assertThat(result.answerStatus()).isEqualTo(AnswerStatus.CORRECT);
        assertThat(result.correctText()).isEqualTo("서울");
        assertThat(result.correctChoices()).isNull();
    }

    @Test
    void 풀이_이력이_저장된다() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(problemRepository.findById(1L)).thenReturn(Optional.of(mcProblem));
        ArgumentCaptor<UserProblemHistory> captor = ArgumentCaptor.forClass(UserProblemHistory.class);

        useCase.execute(new SubmitAnswerCommand(1L, 1L, AnswerType.MULTIPLE_CHOICE, List.of(1, 2), null));

        verify(historyRepository).save(captor.capture());
        assertThat(captor.getValue().getUserId()).isEqualTo(1L);
        assertThat(captor.getValue().getProblemId()).isEqualTo(1L);
        assertThat(captor.getValue().getAnswerStatus()).isEqualTo(AnswerStatus.CORRECT);
    }

    @Test
    void 존재하지_않는_문제면_ProblemNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(problemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new SubmitAnswerCommand(1L, 99L, AnswerType.MULTIPLE_CHOICE, List.of(1), null)))
                .isInstanceOf(ProblemNotFoundException.class);
    }

    @Test
    void 존재하지_않는_사용자면_UserNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(
                new SubmitAnswerCommand(99L, 1L, AnswerType.MULTIPLE_CHOICE, List.of(1), null)))
                .isInstanceOf(UserNotFoundException.class);
    }
}
```

- [ ] **Step 2: 테스트 실행 — FAIL 확인**

```bash
./gradlew :application:test --tests "*.SubmitAnswerUseCaseTest"
```

Expected: FAIL

- [ ] **Step 3: Command/Result + UseCase 구현**

```java
// application/src/main/java/com/jms/jangsomoa/application/problem/SubmitAnswerCommand.java
package com.jms.jangsomoa.application.problem;

import com.jms.jangsomoa.domain.answer.AnswerType;
import java.util.List;

public record SubmitAnswerCommand(
        Long userId,
        Long problemId,
        AnswerType answerType,
        List<Integer> selectedChoices,  // MULTIPLE_CHOICE용. SHORT_ANSWER이면 null
        String text                     // SHORT_ANSWER용. MULTIPLE_CHOICE이면 null
) {}
```

```java
// application/src/main/java/com/jms/jangsomoa/application/problem/SubmitAnswerResult.java
package com.jms.jangsomoa.application.problem;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import java.util.List;

public record SubmitAnswerResult(
        AnswerStatus answerStatus,
        String explanation,
        List<Integer> correctChoices,   // 객관식만. 주관식은 null
        String correctText              // 주관식만. 객관식은 null
) {}
```

```java
// application/src/main/java/com/jms/jangsomoa/application/problem/SubmitAnswerUseCase.java
package com.jms.jangsomoa.application.problem;

import com.jms.jangsomoa.domain.answer.*;
import com.jms.jangsomoa.domain.exception.*;
import com.jms.jangsomoa.domain.history.UserProblemHistory;
import com.jms.jangsomoa.domain.problem.*;
import com.jms.jangsomoa.domain.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubmitAnswerUseCase {
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final UserProblemHistoryRepository historyRepository;

    @Transactional
    public SubmitAnswerResult execute(SubmitAnswerCommand command) {
        userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));
        Problem problem = problemRepository.findById(command.problemId())
                .orElseThrow(() -> new ProblemNotFoundException(command.problemId()));

        AnswerStatus status;
        List<Integer> correctChoices = null;
        String correctText = null;
        UserAnswer userAnswer;

        if (problem instanceof MultipleChoiceProblem mcp) {
            MultipleChoiceAnswer answer = new MultipleChoiceAnswer(command.selectedChoices());
            status = mcp.evaluate(answer);
            userAnswer = answer;
            correctChoices = mcp.getCorrectAnswers();
        } else if (problem instanceof ShortAnswerProblem sap) {
            ShortAnswer answer = new ShortAnswer(command.text());
            status = sap.evaluate(answer);
            userAnswer = answer;
            correctText = sap.getCorrectAnswer();
        } else {
            throw new IllegalStateException("Unknown problem type: " + problem.getClass());
        }

        historyRepository.save(
                UserProblemHistory.create(command.userId(), command.problemId(), status, userAnswer));

        return new SubmitAnswerResult(status, problem.getExplanation(), correctChoices, correctText);
    }
}
```

- [ ] **Step 4: 테스트 PASS 확인**

```bash
./gradlew :application:test --tests "*.SubmitAnswerUseCaseTest"
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add application/
git commit -m "feat: add SubmitAnswerUseCase"
```

---

### Task 6: Application — GetProblemHistoryUseCase

**Files:**
- Create: `application/src/main/java/com/jms/jangsomoa/application/problem/GetProblemHistoryCommand.java`
- Create: `application/src/main/java/com/jms/jangsomoa/application/problem/GetProblemHistoryResult.java`
- Create: `application/src/main/java/com/jms/jangsomoa/application/problem/GetProblemHistoryUseCase.java`
- Test: `application/src/test/java/com/jms/jangsomoa/application/problem/GetProblemHistoryUseCaseTest.java`

**Interfaces:**
- Consumes: Task 2-3의 도메인 클래스 전부
- Produces: `GetProblemHistoryUseCase.execute(GetProblemHistoryCommand): GetProblemHistoryResult`
  - `GetProblemHistoryCommand(Long userId, Long problemId)`
  - `GetProblemHistoryResult(Long problemId, AnswerStatus answerStatus, String explanation, List<Integer> problemAnswers, String problemCorrectText, List<Integer> userChoices, String userText, Integer answerCorrectRate)`

- [ ] **Step 1: 테스트 작성**

```java
// application/src/test/java/com/jms/jangsomoa/application/problem/GetProblemHistoryUseCaseTest.java
package com.jms.jangsomoa.application.problem;

import com.jms.jangsomoa.domain.answer.*;
import com.jms.jangsomoa.domain.exception.*;
import com.jms.jangsomoa.domain.history.UserProblemHistory;
import com.jms.jangsomoa.domain.problem.MultipleChoiceProblem;
import com.jms.jangsomoa.domain.problem.ShortAnswerProblem;
import com.jms.jangsomoa.domain.repository.*;
import com.jms.jangsomoa.domain.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetProblemHistoryUseCaseTest {
    @Mock private UserRepository userRepository;
    @Mock private ProblemRepository problemRepository;
    @Mock private UserProblemHistoryRepository historyRepository;

    @InjectMocks private GetProblemHistoryUseCase useCase;

    private final User user = new User(1L, "테스터");
    private final MultipleChoiceProblem mcProblem = new MultipleChoiceProblem(
            1L, 1L, "문제", "해설", List.of("가", "나", "다", "라", "마"), List.of(1, 2));
    private final ShortAnswerProblem saProblem = new ShortAnswerProblem(
            2L, 1L, "수도는?", "서울", "서울");

    @Test
    void 객관식_이력을_정상_반환한다() {
        UserProblemHistory history = new UserProblemHistory(
                1L, 1L, 1L, AnswerStatus.CORRECT, new MultipleChoiceAnswer(List.of(1, 2)));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(problemRepository.findById(1L)).thenReturn(Optional.of(mcProblem));
        when(historyRepository.findByUserIdAndProblemId(1L, 1L)).thenReturn(Optional.of(history));
        when(historyRepository.findAnswerStatusesByProblemId(1L)).thenReturn(List.of());

        GetProblemHistoryResult result = useCase.execute(new GetProblemHistoryCommand(1L, 1L));

        assertThat(result.problemId()).isEqualTo(1L);
        assertThat(result.answerStatus()).isEqualTo(AnswerStatus.CORRECT);
        assertThat(result.problemAnswers()).containsExactly(1, 2);
        assertThat(result.userChoices()).containsExactly(1, 2);
        assertThat(result.problemCorrectText()).isNull();
        assertThat(result.userText()).isNull();
    }

    @Test
    void 주관식_이력을_정상_반환한다() {
        UserProblemHistory history = new UserProblemHistory(
                1L, 1L, 2L, AnswerStatus.INCORRECT, new ShortAnswer("부산"));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(problemRepository.findById(2L)).thenReturn(Optional.of(saProblem));
        when(historyRepository.findByUserIdAndProblemId(1L, 2L)).thenReturn(Optional.of(history));
        when(historyRepository.findAnswerStatusesByProblemId(2L)).thenReturn(List.of());

        GetProblemHistoryResult result = useCase.execute(new GetProblemHistoryCommand(1L, 2L));

        assertThat(result.problemCorrectText()).isEqualTo("서울");
        assertThat(result.userText()).isEqualTo("부산");
        assertThat(result.problemAnswers()).isNull();
    }

    @Test
    void 이력이_없으면_ProblemHistoryNotFoundException() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(problemRepository.findById(1L)).thenReturn(Optional.of(mcProblem));
        when(historyRepository.findByUserIdAndProblemId(1L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(new GetProblemHistoryCommand(1L, 1L)))
                .isInstanceOf(ProblemHistoryNotFoundException.class);
    }
}
```

- [ ] **Step 2: 테스트 실행 — FAIL 확인**

```bash
./gradlew :application:test --tests "*.GetProblemHistoryUseCaseTest"
```

Expected: FAIL

- [ ] **Step 3: Command/Result + UseCase 구현**

```java
// application/src/main/java/com/jms/jangsomoa/application/problem/GetProblemHistoryCommand.java
package com.jms.jangsomoa.application.problem;

public record GetProblemHistoryCommand(Long userId, Long problemId) {}
```

```java
// application/src/main/java/com/jms/jangsomoa/application/problem/GetProblemHistoryResult.java
package com.jms.jangsomoa.application.problem;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import java.util.List;

public record GetProblemHistoryResult(
        Long problemId,
        AnswerStatus answerStatus,
        String explanation,
        List<Integer> problemAnswers,   // 객관식 정답. 주관식은 null
        String problemCorrectText,      // 주관식 정답. 객관식은 null
        List<Integer> userChoices,      // 객관식 제출 답. 주관식은 null
        String userText,                // 주관식 제출 답. 객관식은 null
        Integer answerCorrectRate
) {}
```

```java
// application/src/main/java/com/jms/jangsomoa/application/problem/GetProblemHistoryUseCase.java
package com.jms.jangsomoa.application.problem;

import com.jms.jangsomoa.domain.answer.*;
import com.jms.jangsomoa.domain.exception.*;
import com.jms.jangsomoa.domain.history.UserProblemHistory;
import com.jms.jangsomoa.domain.problem.*;
import com.jms.jangsomoa.domain.repository.*;
import com.jms.jangsomoa.domain.service.CorrectRateCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetProblemHistoryUseCase {
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final UserProblemHistoryRepository historyRepository;

    @Transactional(readOnly = true)
    public GetProblemHistoryResult execute(GetProblemHistoryCommand command) {
        userRepository.findById(command.userId())
                .orElseThrow(() -> new UserNotFoundException(command.userId()));
        Problem problem = problemRepository.findById(command.problemId())
                .orElseThrow(() -> new ProblemNotFoundException(command.problemId()));
        UserProblemHistory history = historyRepository
                .findByUserIdAndProblemId(command.userId(), command.problemId())
                .orElseThrow(() -> new ProblemHistoryNotFoundException(
                        command.userId(), command.problemId()));

        Integer correctRate = CorrectRateCalculator.calculate(
                historyRepository.findAnswerStatusesByProblemId(command.problemId()));

        List<Integer> problemAnswers = null;
        String problemCorrectText = null;
        List<Integer> userChoices = null;
        String userText = null;

        if (problem instanceof MultipleChoiceProblem mcp) {
            problemAnswers = mcp.getCorrectAnswers();
            userChoices = ((MultipleChoiceAnswer) history.getUserAnswer()).getSelectedChoices();
        } else if (problem instanceof ShortAnswerProblem sap) {
            problemCorrectText = sap.getCorrectAnswer();
            userText = ((ShortAnswer) history.getUserAnswer()).getText();
        }

        return new GetProblemHistoryResult(
                problem.getId(), history.getAnswerStatus(), problem.getExplanation(),
                problemAnswers, problemCorrectText, userChoices, userText, correctRate);
    }
}
```

- [ ] **Step 4: 전체 application 테스트 PASS 확인**

```bash
./gradlew :application:test
```

Expected: BUILD SUCCESSFUL (모든 테스트 PASS)

- [ ] **Step 5: Commit**

```bash
git add application/
git commit -m "feat: add GetProblemHistoryUseCase"
```

---

### Task 7: Infrastructure — JPA 엔티티 + Spring Data 리포지토리 + 컨버터

**Files:**
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/converter/StringListConverter.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/converter/IntegerListConverter.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/problem/ProblemJpaEntity.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/problem/MultipleChoiceProblemJpaEntity.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/problem/ShortAnswerProblemJpaEntity.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/problem/ProblemJpaRepository.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/history/UserProblemHistoryJpaEntity.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/history/UserProblemHistoryJpaRepository.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/ChapterJpaEntity.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/UserChapterSkipJpaEntity.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/ChapterJpaRepository.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/UserChapterSkipJpaRepository.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/user/UserJpaEntity.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/user/UserJpaRepository.java`

**Interfaces:**
- Produces: JPA Entity 클래스 7개, Spring Data JPA Repository 인터페이스 5개, JSON 컨버터 2개

- [ ] **Step 1: 컨버터 작성**

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/converter/StringListConverter.java
package com.jms.jangsomoa.infrastructure.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return attribute == null ? null : MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) { throw new IllegalStateException(e); }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? List.of() : MAPPER.readValue(dbData, new TypeReference<>() {});
        } catch (JsonProcessingException e) { throw new IllegalStateException(e); }
    }
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/converter/IntegerListConverter.java
package com.jms.jangsomoa.infrastructure.jpa.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import java.util.List;

@Converter
public class IntegerListConverter implements AttributeConverter<List<Integer>, String> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<Integer> attribute) {
        try {
            return attribute == null ? null : MAPPER.writeValueAsString(attribute);
        } catch (JsonProcessingException e) { throw new IllegalStateException(e); }
    }

    @Override
    public List<Integer> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? List.of() : MAPPER.readValue(dbData, new TypeReference<>() {});
        } catch (JsonProcessingException e) { throw new IllegalStateException(e); }
    }
}
```

- [ ] **Step 2: Problem JPA 엔티티 작성**

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/problem/ProblemJpaEntity.java
package com.jms.jangsomoa.infrastructure.jpa.problem;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "problem")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "problem_type")
@Getter
@NoArgsConstructor
public abstract class ProblemJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "chapter_id", nullable = false)
    private Long chapterId;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(columnDefinition = "TEXT")
    private String explanation;

    protected ProblemJpaEntity(Long id, Long chapterId, String content, String explanation) {
        this.id = id;
        this.chapterId = chapterId;
        this.content = content;
        this.explanation = explanation;
    }
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/problem/MultipleChoiceProblemJpaEntity.java
package com.jms.jangsomoa.infrastructure.jpa.problem;

import com.jms.jangsomoa.infrastructure.jpa.converter.IntegerListConverter;
import com.jms.jangsomoa.infrastructure.jpa.converter.StringListConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.List;

@Entity
@Table(name = "multiple_choice_problem")
@DiscriminatorValue("MULTIPLE_CHOICE")
@Getter
@NoArgsConstructor
public class MultipleChoiceProblemJpaEntity extends ProblemJpaEntity {
    @Column(nullable = false, columnDefinition = "TEXT")
    @Convert(converter = StringListConverter.class)
    private List<String> choices;

    @Column(name = "correct_answers", nullable = false)
    @Convert(converter = IntegerListConverter.class)
    private List<Integer> correctAnswers;

    public MultipleChoiceProblemJpaEntity(Long id, Long chapterId, String content,
                                           String explanation, List<String> choices,
                                           List<Integer> correctAnswers) {
        super(id, chapterId, content, explanation);
        this.choices = choices;
        this.correctAnswers = correctAnswers;
    }
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/problem/ShortAnswerProblemJpaEntity.java
package com.jms.jangsomoa.infrastructure.jpa.problem;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "short_answer_problem")
@DiscriminatorValue("SHORT_ANSWER")
@Getter
@NoArgsConstructor
public class ShortAnswerProblemJpaEntity extends ProblemJpaEntity {
    @Column(name = "correct_answer", nullable = false)
    private String correctAnswer;

    public ShortAnswerProblemJpaEntity(Long id, Long chapterId, String content,
                                        String explanation, String correctAnswer) {
        super(id, chapterId, content, explanation);
        this.correctAnswer = correctAnswer;
    }
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/problem/ProblemJpaRepository.java
package com.jms.jangsomoa.infrastructure.jpa.problem;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ProblemJpaRepository extends JpaRepository<ProblemJpaEntity, Long> {
    List<ProblemJpaEntity> findByChapterId(Long chapterId);
}
```

- [ ] **Step 3: 나머지 JPA 엔티티/리포지토리 작성**

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/history/UserProblemHistoryJpaEntity.java
package com.jms.jangsomoa.infrastructure.jpa.history;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_problem_history")
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserProblemHistoryJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "problem_id", nullable = false) private Long problemId;

    @Enumerated(EnumType.STRING)
    @Column(name = "answer_status", nullable = false) private AnswerStatus answerStatus;

    @Column(name = "answer_type", nullable = false) private String answerType;
    @Column(name = "answer_value", nullable = false, columnDefinition = "TEXT") private String answerValue;
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/history/UserProblemHistoryJpaRepository.java
package com.jms.jangsomoa.infrastructure.jpa.history;

import com.jms.jangsomoa.domain.answer.AnswerStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface UserProblemHistoryJpaRepository
        extends JpaRepository<UserProblemHistoryJpaEntity, Long> {

    Optional<UserProblemHistoryJpaEntity> findByUserIdAndProblemId(Long userId, Long problemId);

    @Query("SELECT h.problemId FROM UserProblemHistoryJpaEntity h " +
           "JOIN ProblemJpaEntity p ON h.problemId = p.id " +
           "WHERE h.userId = :userId AND p.chapterId = :chapterId")
    List<Long> findSolvedProblemIdsByUserIdAndChapterId(
            @Param("userId") Long userId, @Param("chapterId") Long chapterId);

    @Query("SELECT h.answerStatus FROM UserProblemHistoryJpaEntity h WHERE h.problemId = :problemId")
    List<AnswerStatus> findAnswerStatusesByProblemId(@Param("problemId") Long problemId);
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/ChapterJpaEntity.java
package com.jms.jangsomoa.infrastructure.jpa.chapter;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chapter")
@Getter @NoArgsConstructor @AllArgsConstructor
public class ChapterJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/UserChapterSkipJpaEntity.java
package com.jms.jangsomoa.infrastructure.jpa.chapter;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_chapter_skip",
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "chapter_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserChapterSkipJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "user_id", nullable = false) private Long userId;
    @Column(name = "chapter_id", nullable = false) private Long chapterId;
    @Column(name = "skipped_problem_id", nullable = false) private Long skippedProblemId;
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/ChapterJpaRepository.java
package com.jms.jangsomoa.infrastructure.jpa.chapter;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ChapterJpaRepository extends JpaRepository<ChapterJpaEntity, Long> {}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/UserChapterSkipJpaRepository.java
package com.jms.jangsomoa.infrastructure.jpa.chapter;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserChapterSkipJpaRepository extends JpaRepository<UserChapterSkipJpaEntity, Long> {
    Optional<UserChapterSkipJpaEntity> findByUserIdAndChapterId(Long userId, Long chapterId);
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/user/UserJpaEntity.java
package com.jms.jangsomoa.infrastructure.jpa.user;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @NoArgsConstructor @AllArgsConstructor
public class UserJpaEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false) private String name;
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/user/UserJpaRepository.java
package com.jms.jangsomoa.infrastructure.jpa.user;

import org.springframework.data.jpa.repository.JpaRepository;

public interface UserJpaRepository extends JpaRepository<UserJpaEntity, Long> {}
```

- [ ] **Step 4: 컴파일 확인**

```bash
./gradlew :infrastructure:compileJava
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add infrastructure/
git commit -m "feat: add JPA entities, Spring Data repositories, and JSON converters"
```

---

### Task 8: Infrastructure — Repository 구현체 + 통합 테스트

**Files:**
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/problem/ProblemRepositoryImpl.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/history/UserProblemHistoryRepositoryImpl.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/ChapterRepositoryImpl.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/UserChapterSkipRepositoryImpl.java`
- Create: `infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/user/UserRepositoryImpl.java`
- Test: `infrastructure/src/test/java/com/jms/jangsomoa/infrastructure/jpa/problem/ProblemRepositoryImplTest.java`

**Interfaces:**
- Consumes: Task 7의 JPA 엔티티/리포지토리, Task 3의 domain 인터페이스
- Produces: 모든 domain Repository 인터페이스의 구현체

- [ ] **Step 1: ProblemRepositoryImplTest 작성**

```java
// infrastructure/src/test/java/com/jms/jangsomoa/infrastructure/jpa/problem/ProblemRepositoryImplTest.java
package com.jms.jangsomoa.infrastructure.jpa.problem;

import com.jms.jangsomoa.domain.problem.MultipleChoiceProblem;
import com.jms.jangsomoa.domain.problem.Problem;
import com.jms.jangsomoa.domain.problem.ShortAnswerProblem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(ProblemRepositoryImpl.class)
@Sql(statements = {
    "INSERT INTO chapter (id, name) VALUES (1, '1단원')",
    "INSERT INTO problem (id, chapter_id, content, explanation, problem_type) " +
        "VALUES (1, 1, '객관식문제', '객관식해설', 'MULTIPLE_CHOICE')",
    "INSERT INTO multiple_choice_problem (id, choices, correct_answers) " +
        "VALUES (1, '[\"가\",\"나\",\"다\",\"라\",\"마\"]', '[1,2]')",
    "INSERT INTO problem (id, chapter_id, content, explanation, problem_type) " +
        "VALUES (2, 1, '주관식문제', '주관식해설', 'SHORT_ANSWER')",
    "INSERT INTO short_answer_problem (id, correct_answer) VALUES (2, '서울')"
})
class ProblemRepositoryImplTest {
    @Autowired private ProblemRepositoryImpl problemRepository;

    @Test
    void chapterId로_두_문제_조회() {
        List<Problem> problems = problemRepository.findByChapterId(1L);
        assertThat(problems).hasSize(2);
    }

    @Test
    void 객관식_domain_객체로_변환() {
        Problem problem = problemRepository.findById(1L).orElseThrow();
        assertThat(problem).isInstanceOf(MultipleChoiceProblem.class);
        MultipleChoiceProblem mcp = (MultipleChoiceProblem) problem;
        assertThat(mcp.getChoices()).containsExactly("가", "나", "다", "라", "마");
        assertThat(mcp.getCorrectAnswers()).containsExactly(1, 2);
    }

    @Test
    void 주관식_domain_객체로_변환() {
        Problem problem = problemRepository.findById(2L).orElseThrow();
        assertThat(problem).isInstanceOf(ShortAnswerProblem.class);
        assertThat(((ShortAnswerProblem) problem).getCorrectAnswer()).isEqualTo("서울");
    }

    @Test
    void 존재하지_않는_id면_Optional_empty() {
        Optional<Problem> problem = problemRepository.findById(999L);
        assertThat(problem).isEmpty();
    }
}
```

- [ ] **Step 2: 테스트 실행 — FAIL 확인**

```bash
./gradlew :infrastructure:test --tests "*.ProblemRepositoryImplTest"
```

Expected: FAIL (ProblemRepositoryImpl 없음)

- [ ] **Step 3: Repository 구현체 작성**

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/problem/ProblemRepositoryImpl.java
package com.jms.jangsomoa.infrastructure.jpa.problem;

import com.jms.jangsomoa.domain.problem.*;
import com.jms.jangsomoa.domain.repository.ProblemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProblemRepositoryImpl implements ProblemRepository {
    private final ProblemJpaRepository jpaRepository;

    @Override
    public Optional<Problem> findById(Long id) {
        return jpaRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Problem> findByChapterId(Long chapterId) {
        return jpaRepository.findByChapterId(chapterId).stream().map(this::toDomain).toList();
    }

    private Problem toDomain(ProblemJpaEntity entity) {
        if (entity instanceof MultipleChoiceProblemJpaEntity mce) {
            return new MultipleChoiceProblem(entity.getId(), entity.getChapterId(),
                    entity.getContent(), entity.getExplanation(),
                    mce.getChoices(), mce.getCorrectAnswers());
        } else if (entity instanceof ShortAnswerProblemJpaEntity sae) {
            return new ShortAnswerProblem(entity.getId(), entity.getChapterId(),
                    entity.getContent(), entity.getExplanation(), sae.getCorrectAnswer());
        }
        throw new IllegalStateException("Unknown entity type: " + entity.getClass());
    }
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/history/UserProblemHistoryRepositoryImpl.java
package com.jms.jangsomoa.infrastructure.jpa.history;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jms.jangsomoa.domain.answer.*;
import com.jms.jangsomoa.domain.history.UserProblemHistory;
import com.jms.jangsomoa.domain.repository.UserProblemHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserProblemHistoryRepositoryImpl implements UserProblemHistoryRepository {
    private final UserProblemHistoryJpaRepository jpaRepository;
    private final ObjectMapper objectMapper;

    @Override
    public List<Long> findSolvedProblemIdsByUserIdAndChapterId(Long userId, Long chapterId) {
        return jpaRepository.findSolvedProblemIdsByUserIdAndChapterId(userId, chapterId);
    }

    @Override
    public List<AnswerStatus> findAnswerStatusesByProblemId(Long problemId) {
        return jpaRepository.findAnswerStatusesByProblemId(problemId);
    }

    @Override
    public void save(UserProblemHistory history) {
        String answerType = history.getUserAnswer().getType().name();
        String answerValue = toAnswerValue(history.getUserAnswer());
        jpaRepository.save(new UserProblemHistoryJpaEntity(
                null, history.getUserId(), history.getProblemId(),
                history.getAnswerStatus(), answerType, answerValue));
    }

    @Override
    public Optional<UserProblemHistory> findByUserIdAndProblemId(Long userId, Long problemId) {
        return jpaRepository.findByUserIdAndProblemId(userId, problemId).map(this::toDomain);
    }

    private String toAnswerValue(UserAnswer answer) {
        try {
            if (answer instanceof MultipleChoiceAnswer mca)
                return objectMapper.writeValueAsString(mca.getSelectedChoices());
            if (answer instanceof ShortAnswer sa) return sa.getText();
            throw new IllegalStateException("Unknown answer type");
        } catch (JsonProcessingException e) { throw new IllegalStateException(e); }
    }

    private UserProblemHistory toDomain(UserProblemHistoryJpaEntity entity) {
        return new UserProblemHistory(entity.getId(), entity.getUserId(), entity.getProblemId(),
                entity.getAnswerStatus(), toUserAnswer(entity.getAnswerType(), entity.getAnswerValue()));
    }

    private UserAnswer toUserAnswer(String answerType, String answerValue) {
        try {
            if (AnswerType.MULTIPLE_CHOICE.name().equals(answerType)) {
                List<Integer> choices = objectMapper.readValue(answerValue, new TypeReference<>() {});
                return new MultipleChoiceAnswer(choices);
            }
            return new ShortAnswer(answerValue);
        } catch (JsonProcessingException e) { throw new IllegalStateException(e); }
    }
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/ChapterRepositoryImpl.java
package com.jms.jangsomoa.infrastructure.jpa.chapter;

import com.jms.jangsomoa.domain.chapter.Chapter;
import com.jms.jangsomoa.domain.repository.ChapterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ChapterRepositoryImpl implements ChapterRepository {
    private final ChapterJpaRepository jpaRepository;

    @Override
    public Optional<Chapter> findById(Long id) {
        return jpaRepository.findById(id).map(e -> new Chapter(e.getId(), e.getName()));
    }
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/chapter/UserChapterSkipRepositoryImpl.java
package com.jms.jangsomoa.infrastructure.jpa.chapter;

import com.jms.jangsomoa.domain.repository.UserChapterSkipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserChapterSkipRepositoryImpl implements UserChapterSkipRepository {
    private final UserChapterSkipJpaRepository jpaRepository;

    @Override
    public Optional<Long> findSkippedProblemId(Long userId, Long chapterId) {
        return jpaRepository.findByUserIdAndChapterId(userId, chapterId)
                .map(UserChapterSkipJpaEntity::getSkippedProblemId);
    }

    @Override
    @Transactional
    public void upsert(Long userId, Long chapterId, Long skippedProblemId) {
        jpaRepository.findByUserIdAndChapterId(userId, chapterId).ifPresentOrElse(
                entity -> entity.setSkippedProblemId(skippedProblemId),
                () -> jpaRepository.save(
                        new UserChapterSkipJpaEntity(null, userId, chapterId, skippedProblemId)));
    }
}
```

```java
// infrastructure/src/main/java/com/jms/jangsomoa/infrastructure/jpa/user/UserRepositoryImpl.java
package com.jms.jangsomoa.infrastructure.jpa.user;

import com.jms.jangsomoa.domain.repository.UserRepository;
import com.jms.jangsomoa.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final UserJpaRepository jpaRepository;

    @Override
    public Optional<User> findById(Long id) {
        return jpaRepository.findById(id).map(e -> new User(e.getId(), e.getName()));
    }
}
```

- [ ] **Step 4: 테스트 PASS 확인**

```bash
./gradlew :infrastructure:test
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 5: Commit**

```bash
git add infrastructure/
git commit -m "feat: add repository implementations and infrastructure integration tests"
```

---

### Task 9: API — Controller + DTO + 예외 핸들러 + 통합 테스트

**Files:**
- Create: `api/src/main/java/com/jms/jangsomoa/api/problem/ProblemController.java`
- Create: `api/src/main/java/com/jms/jangsomoa/api/problem/dto/GetRandomProblemRequest.java`
- Create: `api/src/main/java/com/jms/jangsomoa/api/problem/dto/GetRandomProblemResponse.java`
- Create: `api/src/main/java/com/jms/jangsomoa/api/problem/dto/SubmitAnswerRequest.java`
- Create: `api/src/main/java/com/jms/jangsomoa/api/problem/dto/SubmitAnswerResponse.java`
- Create: `api/src/main/java/com/jms/jangsomoa/api/problem/dto/GetProblemHistoryResponse.java`
- Create: `api/src/main/java/com/jms/jangsomoa/api/exception/GlobalExceptionHandler.java`
- Create: `api/src/main/java/com/jms/jangsomoa/api/exception/ErrorResponse.java`
- Create: `api/src/main/resources/data.sql`
- Create: `api/src/test/java/com/jms/jangsomoa/api/problem/ProblemControllerTest.java`
- Create: `api/src/test/resources/application-test.yml`
- Create: `api/src/test/resources/test-data.sql`
- Create: `api/src/test/resources/cleanup.sql`

**Interfaces:**
- Consumes: Task 4-6의 UseCase 3종
- Produces: REST API 3개 엔드포인트 (`POST /api/problems/random`, `POST /api/problems/{id}/submit`, `GET /api/problems/{id}/history`)

- [ ] **Step 1: 통합 테스트 작성**

```java
// api/src/test/java/com/jms/jangsomoa/api/problem/ProblemControllerTest.java
package com.jms.jangsomoa.api.problem;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/test-data.sql")
@Sql(scripts = "/cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class ProblemControllerTest {
    @Autowired private MockMvc mockMvc;

    @Test
    void 랜덤_문제_조회_성공() throws Exception {
        mockMvc.perform(post("/api/problems/random")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"chapterId": 1, "userId": 1}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemId").isNumber())
                .andExpect(jsonPath("$.content").isString());
    }

    @Test
    void 존재하지_않는_사용자_404() throws Exception {
        mockMvc.perform(post("/api/problems/random")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"chapterId": 1, "userId": 999}
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void 존재하지_않는_단원_404() throws Exception {
        mockMvc.perform(post("/api/problems/random")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"chapterId": 999, "userId": 1}
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void 모든_문제를_풀었으면_204() throws Exception {
        // 먼저 두 문제 모두 제출
        mockMvc.perform(post("/api/problems/1/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"userId": 1, "answerType": "MULTIPLE_CHOICE", "userAnswer": [1]}
                        """)).andExpect(status().isOk());
        mockMvc.perform(post("/api/problems/2/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"userId": 1, "answerType": "MULTIPLE_CHOICE", "userAnswer": [2]}
                        """)).andExpect(status().isOk());

        // 그 다음 랜덤 요청 → 204
        mockMvc.perform(post("/api/problems/random")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"chapterId": 1, "userId": 1}
                        """))
                .andExpect(status().isNoContent());
    }

    @Test
    void 객관식_정답_제출_성공() throws Exception {
        mockMvc.perform(post("/api/problems/1/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"userId": 1, "answerType": "MULTIPLE_CHOICE", "userAnswer": [1]}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answerStatus").value("CORRECT"))
                .andExpect(jsonPath("$.explanation").isString())
                .andExpect(jsonPath("$.correctAnswers").isArray());
    }

    @Test
    void 주관식_오답_제출_성공() throws Exception {
        mockMvc.perform(post("/api/problems/3/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"userId": 1, "answerType": "SHORT_ANSWER", "userAnswer": "부산"}
                        """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.answerStatus").value("INCORRECT"));
    }

    @Test
    void 존재하지_않는_문제_제출_404() throws Exception {
        mockMvc.perform(post("/api/problems/999/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"userId": 1, "answerType": "MULTIPLE_CHOICE", "userAnswer": [1]}
                        """))
                .andExpect(status().isNotFound());
    }

    @Test
    void 풀이_이력_조회_성공() throws Exception {
        // 먼저 제출
        mockMvc.perform(post("/api/problems/1/submit")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"userId": 1, "answerType": "MULTIPLE_CHOICE", "userAnswer": [1]}
                        """)).andExpect(status().isOk());

        // 이력 조회
        mockMvc.perform(get("/api/problems/1/history").param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.problemId").value(1))
                .andExpect(jsonPath("$.answerStatus").isString())
                .andExpect(jsonPath("$.problemAnswers").isArray())
                .andExpect(jsonPath("$.userAnswers").isArray());
    }

    @Test
    void 아직_풀지_않은_문제_이력_조회_404() throws Exception {
        mockMvc.perform(get("/api/problems/1/history").param("userId", "1"))
                .andExpect(status().isNotFound());
    }
}
```

- [ ] **Step 2: 테스트용 리소스 파일 작성**

```yaml
# api/src/test/resources/application-test.yml
spring:
  datasource:
    url: jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=MySQL
    driver-class-name: org.h2.Driver
    username: sa
    password:
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false
  sql:
    init:
      mode: embedded
```

```sql
-- api/src/test/resources/test-data.sql
INSERT INTO users (id, name) VALUES (1, '테스터');
INSERT INTO chapter (id, name) VALUES (1, '테스트단원');

INSERT INTO problem (id, chapter_id, content, explanation, problem_type)
VALUES (1, 1, '객관식테스트', '객관식해설', 'MULTIPLE_CHOICE');
INSERT INTO multiple_choice_problem (id, choices, correct_answers)
VALUES (1, '["가","나","다","라","마"]', '[1]');

INSERT INTO problem (id, chapter_id, content, explanation, problem_type)
VALUES (2, 1, '객관식테스트2', '객관식해설2', 'MULTIPLE_CHOICE');
INSERT INTO multiple_choice_problem (id, choices, correct_answers)
VALUES (2, '["a","b","c","d","e"]', '[2]');

INSERT INTO problem (id, chapter_id, content, explanation, problem_type)
VALUES (3, 1, '주관식테스트', '주관식해설', 'SHORT_ANSWER');
INSERT INTO short_answer_problem (id, correct_answer)
VALUES (3, '서울');
```

```sql
-- api/src/test/resources/cleanup.sql
DELETE FROM user_chapter_skip;
DELETE FROM user_problem_history;
DELETE FROM multiple_choice_problem;
DELETE FROM short_answer_problem;
DELETE FROM problem;
DELETE FROM chapter;
DELETE FROM users;
```

- [ ] **Step 3: 테스트 실행 — FAIL 확인**

```bash
./gradlew :api:test --tests "*.ProblemControllerTest"
```

Expected: FAIL (Controller/DTO 없음)

- [ ] **Step 4: DTO 작성**

```java
// api/src/main/java/com/jms/jangsomoa/api/problem/dto/GetRandomProblemRequest.java
package com.jms.jangsomoa.api.problem.dto;

import jakarta.validation.constraints.NotNull;

public record GetRandomProblemRequest(@NotNull Long chapterId, @NotNull Long userId) {}
```

```java
// api/src/main/java/com/jms/jangsomoa/api/problem/dto/GetRandomProblemResponse.java
package com.jms.jangsomoa.api.problem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetRandomProblemResponse(
        Long problemId, String content,
        List<String> choices,        // 객관식만
        Integer answerCorrectRate    // 30명 미만이면 null
) {}
```

```java
// api/src/main/java/com/jms/jangsomoa/api/problem/dto/SubmitAnswerRequest.java
package com.jms.jangsomoa.api.problem.dto;

import jakarta.validation.constraints.NotNull;

public record SubmitAnswerRequest(
        @NotNull Long userId,
        @NotNull String answerType,
        Object userAnswer   // MULTIPLE_CHOICE: List<Integer>, SHORT_ANSWER: String
) {}
```

```java
// api/src/main/java/com/jms/jangsomoa/api/problem/dto/SubmitAnswerResponse.java
package com.jms.jangsomoa.api.problem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jms.jangsomoa.domain.answer.AnswerStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record SubmitAnswerResponse(
        AnswerStatus answerStatus,
        String explanation,
        Object correctAnswers   // 객관식: List<Integer>, 주관식: String
) {}
```

```java
// api/src/main/java/com/jms/jangsomoa/api/problem/dto/GetProblemHistoryResponse.java
package com.jms.jangsomoa.api.problem.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jms.jangsomoa.domain.answer.AnswerStatus;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GetProblemHistoryResponse(
        Long problemId,
        AnswerStatus answerStatus,
        String explanation,
        Object problemAnswers,     // 객관식: List<Integer>, 주관식: String
        Object userAnswers,        // 객관식: List<Integer>, 주관식: String
        Integer answerCorrectRate
) {}
```

- [ ] **Step 5: 예외 핸들러 작성**

```java
// api/src/main/java/com/jms/jangsomoa/api/exception/ErrorResponse.java
package com.jms.jangsomoa.api.exception;

public record ErrorResponse(String message) {}
```

```java
// api/src/main/java/com/jms/jangsomoa/api/exception/GlobalExceptionHandler.java
package com.jms.jangsomoa.api.exception;

import com.jms.jangsomoa.domain.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoAvailableProblemException.class)
    public ResponseEntity<Void> handleNoAvailableProblem() {
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler({ProblemNotFoundException.class, UserNotFoundException.class,
            ChapterNotFoundException.class, ProblemHistoryNotFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(DomainException e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Internal server error"));
    }
}
```

- [ ] **Step 6: Controller 작성**

```java
// api/src/main/java/com/jms/jangsomoa/api/problem/ProblemController.java
package com.jms.jangsomoa.api.problem;

import com.jms.jangsomoa.api.problem.dto.*;
import com.jms.jangsomoa.application.problem.*;
import com.jms.jangsomoa.domain.answer.AnswerType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/problems")
@RequiredArgsConstructor
public class ProblemController {
    private final GetRandomProblemUseCase getRandomProblemUseCase;
    private final SubmitAnswerUseCase submitAnswerUseCase;
    private final GetProblemHistoryUseCase getProblemHistoryUseCase;

    @PostMapping("/random")
    public GetRandomProblemResponse getRandomProblem(
            @Valid @RequestBody GetRandomProblemRequest request) {
        GetRandomProblemResult r = getRandomProblemUseCase.execute(
                new GetRandomProblemCommand(request.userId(), request.chapterId()));
        return new GetRandomProblemResponse(r.problemId(), r.content(), r.choices(), r.answerCorrectRate());
    }

    @PostMapping("/{problemId}/submit")
    public SubmitAnswerResponse submitAnswer(
            @PathVariable Long problemId,
            @Valid @RequestBody SubmitAnswerRequest request) {
        SubmitAnswerCommand command = switch (request.answerType()) {
            case "MULTIPLE_CHOICE" -> new SubmitAnswerCommand(
                    request.userId(), problemId, AnswerType.MULTIPLE_CHOICE,
                    (List<Integer>) request.userAnswer(), null);
            case "SHORT_ANSWER" -> new SubmitAnswerCommand(
                    request.userId(), problemId, AnswerType.SHORT_ANSWER,
                    null, (String) request.userAnswer());
            default -> throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid answerType");
        };
        SubmitAnswerResult r = submitAnswerUseCase.execute(command);
        Object correctAnswers = r.correctChoices() != null ? r.correctChoices() : r.correctText();
        return new SubmitAnswerResponse(r.answerStatus(), r.explanation(), correctAnswers);
    }

    @GetMapping("/{problemId}/history")
    public GetProblemHistoryResponse getProblemHistory(
            @PathVariable Long problemId,
            @RequestParam Long userId) {
        GetProblemHistoryResult r = getProblemHistoryUseCase.execute(
                new GetProblemHistoryCommand(userId, problemId));
        Object problemAnswers = r.problemAnswers() != null ? r.problemAnswers() : r.problemCorrectText();
        Object userAnswers = r.userChoices() != null ? r.userChoices() : r.userText();
        return new GetProblemHistoryResponse(r.problemId(), r.answerStatus(), r.explanation(),
                problemAnswers, userAnswers, r.answerCorrectRate());
    }
}
```

- [ ] **Step 7: 운영 seed data 작성**

```sql
-- api/src/main/resources/data.sql
INSERT INTO users (id, name) VALUES (1, '홍길동');
INSERT INTO users (id, name) VALUES (2, '김철수');

INSERT INTO chapter (id, name) VALUES (1, '1단원: 조선시대');
INSERT INTO chapter (id, name) VALUES (2, '2단원: 고려시대');

INSERT INTO problem (id, chapter_id, content, explanation, problem_type)
VALUES (1, 1, '조선을 건국한 인물은?', '이성계가 1392년 조선을 건국하였습니다.', 'MULTIPLE_CHOICE');
INSERT INTO multiple_choice_problem (id, choices, correct_answers)
VALUES (1, '["이성계","이방원","정도전","황희","세종대왕"]', '[1]');

INSERT INTO problem (id, chapter_id, content, explanation, problem_type)
VALUES (2, 1, '조선의 기본 법전은?', '경국대전은 조선의 기본 법전입니다.', 'MULTIPLE_CHOICE');
INSERT INTO multiple_choice_problem (id, choices, correct_answers)
VALUES (2, '["경국대전","삼국사기","고려사","동국통감","속대전"]', '[1]');

INSERT INTO problem (id, chapter_id, content, explanation, problem_type)
VALUES (3, 1, '훈민정음을 창제한 왕은?', '세종대왕이 1443년 훈민정음을 창제하였습니다.', 'SHORT_ANSWER');
INSERT INTO short_answer_problem (id, correct_answer) VALUES (3, '세종대왕');

INSERT INTO problem (id, chapter_id, content, explanation, problem_type)
VALUES (4, 2, '고려를 건국한 인물은?', '왕건이 918년 고려를 건국하였습니다.', 'SHORT_ANSWER');
INSERT INTO short_answer_problem (id, correct_answer) VALUES (4, '왕건');
```

- [ ] **Step 8: 전체 테스트 PASS 확인**

```bash
./gradlew :api:test
```

Expected: BUILD SUCCESSFUL (모든 테스트 PASS)

- [ ] **Step 9: 전체 빌드 확인**

```bash
./gradlew build
```

Expected: BUILD SUCCESSFUL

- [ ] **Step 10: Commit**

```bash
git add api/
git commit -m "feat: add REST API controller, DTOs, exception handler, and integration tests"
```

---

## Self-Review

**Spec coverage 확인:**
- ✅ POST /api/problems/random → Task 4 + 9
- ✅ POST /api/problems/{id}/submit → Task 5 + 9
- ✅ GET /api/problems/{id}/history → Task 6 + 9
- ✅ 204 (풀 문제 없음) → NoAvailableProblemException → GlobalExceptionHandler
- ✅ 404 (없는 사용자/문제/단원) → 각 NotFoundException → GlobalExceptionHandler
- ✅ JOINED 상속 전략 → Task 7 ProblemJpaEntity
- ✅ UserChapterSkip upsert → Task 8 UserChapterSkipRepositoryImpl
- ✅ 정답률 30명 기준 + PARTIAL 오답 처리 → Task 2 CorrectRateCalculator
- ✅ domain 순수 Java (Spring/JPA 없음) → Task 2-3
- ✅ 단위 테스트 (domain + application) → Task 2, 4, 5, 6
- ✅ 통합 테스트 (infrastructure @DataJpaTest) → Task 8
- ✅ 통합 테스트 (api @SpringBootTest) → Task 9
- ✅ seed data → Task 9 data.sql
