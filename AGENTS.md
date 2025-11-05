# VANITAS 프로젝트 개요

## 저장소 구조
- `settings.gradle.kts`는 루트 프로젝트명을 `demo`로 지정하고 컨벤션 빌드(`build-logic`)를 등록한 뒤 단일 하위 모듈 `Example`을 포함합니다.
- `build-logic/`에는 Kotlin/JVM 및 Spring Boot 설정을 표준화하는 사전 컴파일된 Gradle 컨벤션 플러그인(`common`, `app`)이 있습니다.
- `example/`(Gradle 모듈명 `Example`)은 현재 유일한 애플리케이션 모듈이며, `com.nhn.inje.ccp` 패키지에 최소한의 Spring Boot 부트스트랩 클래스를 포함합니다.

## 빌드 컨벤션
- 모든 모듈은 자동으로 `common` 플러그인을 적용하여 Kotlin JVM 플러그인 활성화, Java 21 타깃, JUnit 5(`useJUnitPlatform`) 설정을 공유합니다.
- 루트 빌드는 `org.jlleitschuh.gradle.ktlint`를 적용하여 스타일 검사를 제공하며 `./gradlew ktlintCheck`로 실행할 수 있습니다.
- `app` 플러그인은 Spring Boot 관련 플러그인(`org.springframework.boot`, `io.spring.dependency-management`, Kotlin Spring/all-open)을 추가하고 버전 카탈로그에 정의된 기본 웹 스타터 의존성을 주입합니다.
- `bootJar` 작업은 활성화하고 일반 `jar` 작업은 비활성화해 중복 아카이브 생성을 방지합니다.

## 의존성 관리
- `gradle/libs.versions.toml`에서 Kotlin 2.1.21, Spring Boot 3.5.4, 테스트(Kotest, MockK, Instancio), 데이터베이스(PostgreSQL, p6spy), 보안/OAuth 스타터 등 공통 버전을 중앙에서 관리합니다.
- `app` 컨벤션 플러그인이 자주 쓰이는 애플리케이션 의존성을 미리 묶어두었으며, 모듈별 추가 의존성은 해당 모듈의 `build.gradle.kts`에 선언하면 됩니다.

## 애플리케이션 모듈(`Example`)
- `app` 플러그인만 적용하고 있으며, 런타임/테스트 의존성은 모두 컨벤션에서 상속받습니다.
- `Main.kt`는 별도 컨트롤러나 설정 없이 Spring Boot 애플리케이션을 기동하는 기본 진입점만 제공합니다.

## 빌드 및 실행
- 제공된 Gradle 래퍼를 사용하세요: `./gradlew build`로 컴파일 및 테스트(현재는 없음)를 수행합니다.
- Spring Boot 실행은 `./gradlew :Example:bootRun`, 패키징은 `./gradlew :Example:bootJar`로 수행할 수 있습니다.

## 주요 관찰 및 제안
- 포함된 모듈 이름이 `Example`(대문자)인 반면 디렉터리는 `example/`(소문자)라서 대소문자 구분이 있는 리눅스 환경에서는 문제가 될 수 있으니 정렬을 고려하세요.
- 도메인 로직, 환경 설정, 테스트 코드가 아직 없으므로 현재 저장소는 빌드 구성 템플릿 역할에 가깝습니다.
- 서비스별로 다른 의존성 구성이 필요하다면 모듈 빌드 스크립트에서 컨벤션을 기반으로 확장하십시오.
