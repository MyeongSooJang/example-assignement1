# Trouble Shooting

## 1. JAVA_HOME 미설정

**Problem**
```
ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH.
```
IntelliJ는 내장 JDK를 사용하지만 시스템 환경변수에 등록이 안 된 상태.
PowerShell/CMD는 시스템 환경변수를 따르므로 `gradlew.bat` 실행 불가.

CMD에서 영구 설정 후 IntelliJ 재시작:
```cmd
setx JAVA_HOME "C:\Users\mmsgo\.jdks\ms-21.0.8"
```

---

## 2. gradlew 실행 오류 (Windows)

**Problem**
```
'""'은(는) 내부 또는 외부 명령, 실행할 수 있는 프로그램, 또는 배치 파일이 아닙니다.
```
`./gradlew`는 Linux/Mac용 shell 스크립트. Windows에서는 `gradlew.bat` 사용:
```powershell
.\gradlew.bat :api:bootJar
```

---

## 3. MySQL 포트 충돌

**Problem**
```
Error response from daemon: ports are not available: exposing port TCP 0.0.0.0:3306
bind: Only one usage of each socket address is normally permitted.
```
로컬에 설치된 MySQL이 이미 3306을 점유 중. `docker-compose.yml`에서 로컬 포트를 3307로 변경:
```yaml
ports:
  - "3307:3306"
```

---

## 4. data.sql 파일 없음

**Problem**
```
No data scripts found at location 'classpath:data.sql'
```
`application.yml`에 `data-locations: classpath:data.sql` 설정이 있는데 파일이 없음.
`api/src/main/resources/data.sql` 생성으로 해결.

---

## 5. MySQL healthcheck 타이밍 문제

**Problem**
healthcheck는 통과했지만 앱이 MySQL 연결 실패 (`Connection refused`).

`mysqladmin ping`은 MySQL 프로세스가 살아있기만 하면 통과해서 실제 연결 준비 전에 앱이 시작됨.
healthcheck를 실제 연결 테스트로 변경:
```yaml
healthcheck:
  test: ["CMD", "mysql", "-h", "localhost", "-u", "assignment", "-passignment", "-e", "SELECT 1"]
  start_period: 10s
```

---

## 6. data.sql 실행 순서 문제

**Problem**
```
java.sql.SQLSyntaxErrorException: Table 'assignment1.users' doesn't exist
```
Spring Boot 2.5+부터 `data.sql`이 JPA 스키마 생성(`ddl-auto: create`)보다 먼저 실행됨.
`application.yml`에 아래 설정 추가로 순서 변경:
```yaml
spring:
  jpa:
    defer-datasource-initialization: true
```
