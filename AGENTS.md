# Repository Guidelines

## 프로젝트 구조 및 모듈 구성
루트 경로의 Gradle Kotlin DSL 래퍼가 모든 모듈을 오케스트레이션합니다. 현재 버전에 포함된 `example` 모듈은 Spring Boot 서비스이며, Kotlin 소스는 `example/src/main/kotlin`(`com.nhn.inje.ccp` 패키지) 아래에, 설정 리소스는 `example/src/main/resources` 아래에 둡니다. 테스트 코드는 `example/src/test/kotlin`으로, 컴파일 산출물과 정적 분석 리포트는 각 모듈의 `build/` 및 루트의 `build/reports`에 생성됩니다.

## 빌드·테스트·개발 명령
- `./gradlew clean build` — 모든 모듈을 컴파일하고 전체 테스트를 실행한 뒤 실행 가능한 아티팩트를 만듭니다.
- `./gradlew :example:bootRun` — Spring Boot 애플리케이션을 로컬 설정으로 기동하여 수동 확인에 사용합니다.
- `./gradlew :example:test` — `example` 모듈 테스트만 수행하여 빠른 피드백을 제공합니다.
- `./gradlew ktlintCheck` / `./gradlew ktlintFormat` — 공통 `common` 컨벤션 플러그인을 통해 Kotlin 스타일을 검사하거나 자동 정렬합니다.
- `./gradlew :example:bootJar` — 배포 또는 컨테이너 패키징에 사용할 실행 Jar를 생성합니다.

## 코딩 스타일 및 네이밍 컨벤션
4스페이스 들여쓰기를 사용하고 `var`보다 `val`을 우선하며, 간단한 반환은 표현식 본문 함수를 선호합니다. 패키지는 경로 구조(`com.nhn.inje.ccp.<feature>`)와 일치시키고, 클래스는 UpperCamelCase, 메서드·필드는 lowerCamelCase, 상수는 SCREAMING_SNAKE_CASE를 적용합니다. 푸시 전에는 반드시 ktlint를 실행해 CI 차단을 예방하세요. 저장소 전역 `common` 플러그인이 각 빌드에 해당 검사를 연결합니다.

## 테스트 가이드라인
애플리케이션 컨벤션 플러그인이 제공하는 Spring Boot + JUnit 5 스택을 사용합니다. 테스트 클래스는 `<Subject>Test`, 메서드는 `fun shouldReturn...()`처럼 동작을 드러내는 이름으로 작성합니다. 공통 픽스처는 `example/src/test/resources`에 배치하고, 협력 객체는 가벼운 목으로 대체하며, 컨테이너 배선을 확인해야 할 때에만 `@SpringBootTest`를 사용합니다. 배포 전에는 항상 `./gradlew test`(또는 모듈 범위 명령)를 실행하고, `build/reports/tests/test/index.html`을 열어 성공/실패와 커버리지 노트를 확인하십시오.

## 커밋 및 PR 가이드라인
기록에는 `chore: 모듈 초기화`처럼 컨벤션을 따른 메시지와 `update`처럼 모호한 메시지가 섞여 있습니다. 리뷰어가 의도를 빠르게 파악할 수 있도록 `feat: add webhook controller` 형식의 `<type>: <명령형 요약>`을 표준으로 맞추세요. PR에는 변경 요약, 관련 이슈 링크, 마이그레이션이나 설정 변경 사항, 수동 검증 단계(실행 명령, UI 변경 시 스크린샷)를 포함합니다. 항상 최신 `main`을 기준으로 리베이스하고 `./gradlew clean build`와 ktlint 검사를 통과한 뒤, 공유 Gradle 플러그인이나 빌드 로직을 수정했다면 해당 영역 담당 리뷰어를 요청하세요.
