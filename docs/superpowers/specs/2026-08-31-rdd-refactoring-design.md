# RDD 기반 전체 레이어 리팩터링 설계

## 목표

책임 주도 설계(Responsibility-Driven Design) 원칙을 적용해 현재 코드의 객체 책임을 재검토하고 개선한다.
DDD 멀티모듈 구조(domain → application → infrastructure ← api)는 유지한다.

## 접근 방식

유스케이스 시나리오 중심(Top-down). 시나리오 흐름에서 어떤 메시지가 어떤 객체에게 전달되는지를 추적하고, 그 메시지를 가장 자연스럽게 처리할 수 있는 객체에게 책임을 배분한다.

## 진행 순서

| 순서 | 시나리오 | 주요 관여 객체 |
|---|---|---|
| 1 | 랜덤 문제 조회 | Problem, Chapter, UserProblemHistory, UserChapterSkip |
| 2 | 문제 제출 | Problem, Answer, UserProblemHistory |
| 3 | 풀이 이력 조회 | UserProblemHistory, Problem |

1번부터 시작하는 이유: 가장 많은 객체가 협력하고 책임 배분이 복잡하다. 여기서 결정된 책임이 2번, 3번의 설계 기준이 된다.

## 각 시나리오 진행 방식

1. 시나리오 흐름 공유 — 해당 유스케이스에서 어떤 일이 일어나는지 확인
2. 사용자가 객체 하나를 잡고 책임을 제안
3. Claude가 RDD 원칙 기준으로 평가 — 맞으면 확인, 틀리면 왜 틀렸는지 설명
4. 납득하면 코드로 옮김

## Claude 행동 규칙

- 사용자가 책임을 제안하기 전에 답을 먼저 제시하지 않는다
- 틀렸을 때 바로 정답을 알려주지 않고, 왜 틀렸는지 이해할 수 있도록 질문하거나 설명한다
- RDD 원칙에 맞지 않으면 비유나 예시로 설명한다

## 범위

- 레이어: domain, application, api, infrastructure 전체
- 시작: domain 객체 책임부터 정의 후 위 레이어로 올라감
- 제외: 인프라 설정(Docker, DB 스키마), Swagger 문서화
