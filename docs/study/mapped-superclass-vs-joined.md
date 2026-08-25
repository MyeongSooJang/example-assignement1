# @MappedSuperclass vs @Inheritance(JOINED) 비교

## 핵심 판단 기준

> **부모 타입으로 조회할 일이 있는가?**

- 없다 → `@MappedSuperclass`
- 있다 → `@Inheritance(JOINED)`

---

## @MappedSuperclass

공통 컬럼 재사용이 목적. 부모 테이블이 생기지 않고 자식 테이블에 컬럼이 복사된다.

```java
@MappedSuperclass
public abstract class BaseEntity {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

@Entity
public class UserEntity extends BaseEntity { ... }
// users 테이블: id, name, createdAt, updatedAt

@Entity
public class OrderEntity extends BaseEntity { ... }
// orders 테이블: id, amount, createdAt, updatedAt
```

### DB 구조

```
users 테이블                orders 테이블
──────────────────          ──────────────────
id                          id
name                        amount
created_at                  created_at
updated_at                  updated_at
```

`base_entity` 테이블은 존재하지 않는다.

### 한계

부모 타입으로 다형성 조회가 불가능하다.

```java
// 불가능 — problems 테이블이 없기 때문
problemRepository.findById(id);

// 타입을 미리 알아야 조회 가능
multipleChoiceProblemRepository.findById(id);
shortAnswerProblemRepository.findById(id);
```

---

## @Inheritance(JOINED)

부모 테이블과 자식 테이블이 모두 생성된다. 조회 시 JPA가 자동으로 JOIN한다.

```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class ProblemEntity { ... }

@Entity
public class MultipleChoiceProblemEntity extends ProblemEntity { ... }

@Entity
public class ShortAnswerProblemEntity extends ProblemEntity { ... }
```

### DB 구조

```
problems 테이블                    multiple_choice_problems 테이블
──────────────────                 ──────────────────────────────
id                                 id (FK → problems.id)
chapter_id                         choices
content                            correct_answers
solution
dtype
```

### 조회 시 실행되는 SQL

```sql
SELECT p.*, m.choices, m.correct_answers
FROM problems p
LEFT JOIN multiple_choice_problems m ON p.id = m.id
LEFT JOIN short_answer_problems s ON p.id = s.id
WHERE p.id = 1
```

### 장점

타입을 모른 채 부모 타입으로 단일 ID 조회가 가능하다.

```java
// problemId만 알면 어떤 타입이든 조회 가능
Problem problem = problemRepository.findById(id);
```

### 단점

항상 JOIN이 발생하므로 SINGLE_TABLE 전략보다 성능이 느리다.

---

## 비교 요약

| | @MappedSuperclass | @Inheritance(JOINED) |
|---|---|---|
| 부모 테이블 생성 | X | O |
| 부모 타입으로 조회 | X | O |
| JOIN 발생 | X | O |
| null 컬럼 | X | X |
| 주 목적 | 공통 컬럼 재사용 | 다형성 조회 |

---

## 이 프로젝트에서 JOINED를 선택한 이유

`SubmitAnswer` 유스케이스에서 `problemId`만 받아 채점을 수행한다.
이때 문제가 객관식인지 주관식인지 미리 알 수 없으므로, 부모 타입(`ProblemEntity`)으로 조회한 뒤 다형성으로 처리해야 한다.
`@MappedSuperclass`는 부모 테이블이 없어 이 조회가 불가능하기 때문에 JOINED 전략을 선택했다.
