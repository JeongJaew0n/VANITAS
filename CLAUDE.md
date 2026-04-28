# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository Intent

VANITAS는 헥사고날 아키텍처(Ports & Adapters) 적용을 염두에 두고 **Gradle 멀티 모듈**로 구성한 학습/실험용 프로젝트다. 다만 현재는 의도적으로 단일 모듈 `example`에서만 구현이 진행되며, 추후 서브도메인이 충분히 커지면 별도 모듈로 승격한다. 따라서 새 코드는 기본적으로 `example` 모듈 안에 둔다 — 구조를 확장할 때 비로소 모듈을 분리한다.

`example` 내부의 패키지 구조는 **subdomain-first** 로 채택됐다(`example/<sub>/{adapter,application,domain}`). 결정 배경·운영 규칙·승격 시그널은 `docs/hexagonal-structure-decision.md`에 정리돼 있다 — 새 코드 작성 전에 반드시 참고할 것.

## 빌드/실행/테스트 명령

루트의 Gradle 래퍼만 사용한다.

```bash
./gradlew clean build                      # 전체 컴파일 + 테스트 + 산출물
./gradlew :example:bootRun                 # 8080 포트로 Spring Boot 기동
./gradlew :example:test                    # example 모듈 테스트만
./gradlew :example:test --tests "*EchoControllerTest"   # 단일 테스트 클래스
./gradlew :example:test --tests "*EchoControllerTest.shouldReturn*"  # 단일 메서드
./gradlew :example:bootJar                 # 실행 가능한 JAR (Dockerfile 도 이걸 사용)
./gradlew ktlintCheck                      # 모든 모듈 스타일 검사 (CI 게이트)
./gradlew ktlintFormat                     # 자동 정렬
```

테스트 리포트: `example/build/reports/tests/test/index.html`.

`example/src/test/kotlin` 디렉토리는 아직 존재하지 않는다 — 첫 테스트 추가 시 생성해야 한다.

Docker: 루트에서 `docker build -f example/Dockerfile .` 으로 빌드 (Dockerfile은 루트 컨텍스트를 가정한다).

## 멀티 모듈 빌드 구조

`./gradlew :<module>:<task>` 형태로 호출. 새 모듈을 추가할 때는 다음 흐름을 따른다.

1. **`settings.gradle.kts`**: `include("<module>")` 등록.
2. **`build.gradle.kts` (루트)**: `subprojects { apply(plugin = "jvm"); apply(plugin = "ktlint") }`로 모든 모듈에 공통 컨벤션을 강제. `group`/`version`도 여기서 상속됨.
3. **`buildSrc/src/main/kotlin/`**: 공통 컨벤션 플러그인이 사는 곳.
   - `jvm.gradle.kts` → JVM 21 toolchain, JSR-305 strict, JUnit Platform
   - `ktlint.gradle.kts` → `org.jlleitschuh.gradle.ktlint` 적용
   - 새로운 공통 규약(예: testFixtures, jacoco)은 여기에 새 `*.gradle.kts`를 추가하고 루트에서 `apply` 하는 식으로 확장한다.
4. **`gradle/libs.versions.toml`**: 모든 버전·라이브러리·플러그인은 여기서 카탈로그로 관리. 모듈 `build.gradle.kts`는 `libs.spring.boot.starter.web` 처럼 alias로만 참조한다 — 모듈 파일에 버전 문자열을 직접 쓰지 말 것. 카탈로그의 하이픈은 Kotlin DSL에서 점(`.`)으로 접근된다.
5. **모듈 `build.gradle.kts`**: 모듈 고유 플러그인(`spring-boot`, `kotlin-spring` 등)과 의존성만 선언. JVM/ktlint 컨벤션은 자동 상속이므로 재선언 금지.

서브도메인을 별도 모듈로 승격할 때는 (역시 subdomain-first) `user-domain`(순수 Kotlin) / `user-application` / `user-adapter` 묶음으로 쪼갠다. 이 시점이 `buildSrc`에 모듈 타입별 컨벤션 플러그인(예: `domain.gradle.kts` — Spring 의존성 없음)을 추가할 타이밍. 승격 판단 기준은 `docs/hexagonal-structure-decision.md`의 "승격 시그널" 절을 따른다.

## 소스 레이아웃 / 패키지 컨벤션

- **패키지 루트는 `com`.** 옛 경로 `com.nhn.inje.ccp.*`를 다시 만들지 말 것. Maven 좌표 `group = "com.nhn.inje.ccp"` (루트 `build.gradle.kts`)는 **별개** 개념이라 패키지와 일치하지 않아도 정상이며 임의로 바꾸지 않는다.
- **서브도메인이 1차 축, 헥사고날 3계층이 2차 축.** 새 코드는 `com.<subdomain>.{adapter,application,domain}` 형태로 둔다 (예: `com.user.adapter.in.web.UserController`). 계층을 1차 축으로 쓰는 layer-first(`com.adapter.user.*`)는 채택하지 않았다 — 이유는 `docs/hexagonal-structure-decision.md`.
- **현재 임시 코드 위치**: `com.Main`, `com.controller.{Chaos,Echo,Health}Controller`는 헥사고날 도입 이전의 데모 컨트롤러다. 새로 작성하는 비즈니스 코드는 위 subdomain-first 규칙을 따르고, 기존 데모는 서브도메인 정착 시 정리한다.
- **각 서브도메인 내부 규칙** (decision doc 요약):
  - `<sub>/domain/`엔 순수 객체만. Spring/JPA 어노테이션 금지.
  - `<sub>/application/`엔 usecase + port 인터페이스(in/out 모두). port는 안쪽이 소유.
  - `<sub>/adapter/`엔 port 구현만. `in/web`, `out/persistence`, `out/client` 식으로 한 번 더 나눈다.
  - 서브도메인 간 호출은 **port를 통해서만**. `order/application`이 `user/application`의 service를 직접 import 금지 — outport 정의 후 어댑터에서 연결하거나 도메인 이벤트로 비동기 통신.
  - 공용 ID/VO/도메인 이벤트만 `com.shared/`에 둔다. 공용 service·usecase는 두지 않는다 (비대해지면 컨텍스트 분할 실패 신호).

## 코딩/테스트/커밋 규약

`AGENTS.md`에 정의된 것을 그대로 따른다 — 핵심만:

- 4-스페이스 들여쓰기, `val` 우선, 표현식 본문 함수 선호.
- 클래스 `UpperCamelCase`, 메서드/필드 `lowerCamelCase`, 상수 `SCREAMING_SNAKE_CASE`.
- 테스트 클래스 `<Subject>Test`, 메서드 `fun shouldReturn...()`. `@SpringBootTest`는 컨테이너 배선 검증이 필요할 때만.
- 커밋 메시지: `<type>: <명령형 요약>` (예: `feat: add webhook controller`). 푸시 전 `./gradlew ktlintCheck`와 `./gradlew clean build` 통과 필수.

## 알려진 함정

- **`kotlin-allopen` vs `kotlin-spring` 충돌**: `buildSrc/build.gradle.kts`가 `kotlin-allopen`을 의존하고 있어, 모듈에서 `alias(libs.plugins.kotlin.spring)`로 플러그인을 적용하면 해석 충돌이 발생할 수 있다. 현재 `example/build.gradle.kts`는 우회책으로 버전을 생략한 `kotlin("plugin.spring")`을 쓴다 — 새 Spring 모듈 추가 시 같은 패턴을 사용하거나, `buildSrc`에서 `kotlin-allopen`을 제거하는 근본 수정을 고려.
- **`subprojects` 블록의 `apply(plugin = "jvm")` / `apply(plugin = "ktlint")`**: `buildSrc`가 컴파일된 후에야 이 플러그인 ID들이 해석된다. `buildSrc/build.gradle.kts`나 `gradle/libs.versions.toml`을 건드린 뒤 빌드가 깨지면 `./gradlew --stop && ./gradlew clean build`로 데몬을 재시작해 본다.

## 보조 문서

- `docs/hexagonal-structure-decision.md` — **패키지/모듈 구조의 단일 출처.** subdomain-first 채택 근거, 운영 규칙 5개, 승격 시그널. 구조 관련 작업 전 필독.
- `docs/websocket-guide.md` — WebSocket 추가 절차. 기능별 가이드 작성 위치의 본보기.
- `PROJECT_CONTEXT.md` — 모듈/버전/의존성 스냅샷. 구조를 크게 바꾸면 같이 갱신.
- `AGENTS.md` — 스타일/테스트/PR 가이드 원본.
