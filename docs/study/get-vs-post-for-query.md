# 조회 API에서 GET vs POST

## 핵심 원칙

> 서버 상태를 변경하지 않는 조회는 GET을 사용한다.

---

## GET

```
GET /api/problems/random?chapterId=1&userId=1
```

- REST 원칙에 충실 (멱등성, 캐싱 가능)
- 파라미터가 URL에 노출됨 → 민감 정보가 아니라면 문제 없음
- 브라우저 캐싱, 북마크 가능

## POST (조회에 사용하는 경우)

```
POST /api/problems/random
{ "chapterId": 1, "userId": 1 }
```

- REST 원칙상 조회에는 맞지 않음
- 파라미터가 복잡하거나 많을 때 실용적으로 선택하기도 함
- Elasticsearch 검색 API가 대표적인 사례

---

## 판단 기준

| 상황 | 선택 |
|---|---|
| 파라미터가 단순하고 민감하지 않음 | GET |
| 파라미터가 복잡하거나 중첩 구조 | POST 고려 |
| 민감 정보 포함 (비밀번호 등) | POST |

---

## 이 프로젝트에서의 결정

`chapterId`, `userId` 두 개 파라미터로 단순하고 민감 정보도 아니기 때문에 GET으로 설계했다.

요구사항 예시가 Body 형태로 주어졌더라도, 조회 의미에 맞는 HTTP 메서드를 선택하는 것이 REST 원칙에 부합한다.
