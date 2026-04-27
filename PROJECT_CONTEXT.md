# VANITAS 프로젝트 컨텍스트

## 프로젝트 개요
- **프로젝트명**: VANITAS (daily project)
- **그룹**: com.nhn.inje.ccp
- **버전**: 0.0.1-SNAPSHOT
- **빌드 시스템**: Gradle (Kotlin DSL)
- **언어**: Kotlin 2.0.21
- **JVM 버전**: 21

## 아키텍처

### 멀티 모듈 구조
현재 프로젝트는 Gradle 멀티 모듈 구조로 설정되어 있으며, 다음과 같은 계층 구조를 따릅니다:

```
VANITAS/
├── build.gradle.kts          # 루트 빌드 파일 (그룹/버전 정의)
├── settings.gradle.kts        # 모듈 등록 및 저장소 설정
├── gradle/
│   └── libs.versions.toml    # 버전 카탈로그 (중앙 집중식 의존성 관리)
├── buildSrc/                  # 공유 빌드 로직
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       ├── jvm.gradle.kts    # Kotlin/JVM 공통 컨벤션
│       └── ktlint.gradle.kts # ktlint 공통 컨벤션
└── example/                   # Spring Boot 서비스 모듈
    ├── build.gradle.kts
    └── src/main/kotlin/com/nhn/inje/ccp/
        └── Main.kt
```

### 빌드 설정 흐름
1. **settings.gradle.kts**: 루트 프로젝트와 하위 모듈을 Gradle에 등록
2. **build.gradle.kts (루트)**: group/version을 정의하고 모든 서브프로젝트에 공통 플러그인 적용
3. **buildSrc**: 모든 모듈이 공유할 빌드 로직을 컴파일
4. **buildSrc/src/main/kotlin/**: 공통 컨벤션 플러그인 정의
   - `jvm.gradle.kts`: Kotlin/JVM 컨벤션 (JVM 21, JUnit Platform)
   - `ktlint.gradle.kts`: 코드 스타일 검사 컨벤션
5. **gradle/libs.versions.toml**: 버전 카탈로그를 통한 중앙 집중식 의존성 관리
6. **example/build.gradle.kts**: 개별 모듈이 Spring Boot 구성을 상속

## 현재 모듈

### example 모듈
- **타입**: Spring Boot 웹 애플리케이션
- **패키지**: com
- **메인 클래스**: Main.kt
- **의존성**:
  - spring-boot-starter-web
  - spring-boot-starter-test (test)

## 의존성 버전 (libs.versions.toml)

### 버전
- Kotlin: 2.0.21
- ktlint: 12.1.1
- Spring Boot: 3.5.4
- Spring Dependency Management: 1.1.6

### 라이브러리
- kotlin-gradle-plugin
- kotlin-allopen
- ktlint-gradle
- spring-boot-starter-web
- spring-boot-starter-test

### 플러그인
- spring-boot
- spring-dependency-management
- kotlin-spring

## 빌드 컨벤션

### Kotlin/JVM 설정 (jvm.gradle.kts)
- JVM Toolchain: 21
- JVM Target: JVM_21
- Compiler Args: `-Xjsr305=strict`
- Test Framework: JUnit Platform

### ktlint 설정 (ktlint.gradle.kts)
- 모든 모듈에 자동 적용
- 버전: 12.1.1

## 주요 Gradle 명령어

### 빌드 & 테스트
- `./gradlew clean build` - 전체 빌드 및 테스트
- `./gradlew :example:bootRun` - Spring Boot 애플리케이션 실행
- `./gradlew :example:test` - example 모듈 테스트만 실행
- `./gradlew :example:bootJar` - 실행 가능한 Jar 생성

### 코드 스타일
- `./gradlew ktlintCheck` - 코드 스타일 검사
- `./gradlew ktlintFormat` - 코드 스타일 자동 정렬

## 코딩 컨벤션

### 네이밍
- 클래스: UpperCamelCase
- 메서드/필드: lowerCamelCase
- 상수: SCREAMING_SNAKE_CASE
- 패키지: 경로 구조와 일치 (com.<feature>)

### 스타일
- 들여쓰기: 4 스페이스
- `var`보다 `val` 우선
- 간단한 반환은 표현식 본문 함수 선호

### 테스트
- Framework: JUnit 5 + Spring Boot Test
- 테스트 클래스: `<Subject>Test`
- 테스트 메서드: `fun shouldReturn...()`
- `@SpringBootTest`는 컨테이너 배선 확인 시에만 사용

## 커밋 컨벤션
- 형식: `<type>: <명령형 요약>`
- 예시: `feat: add webhook controller`, `chore: 모듈 초기화`

## 알려진 이슈 및 해결 방법

### kotlin-spring 플러그인 충돌
**문제**: `Error resolving plugin [id: 'org.jetbrains.kotlin.plugin.spring', version: '2.0.21']`
- buildSrc에 `kotlin-allopen` 의존성이 있으면 kotlin-spring 플러그인과 충돌 발생
- `kotlin-allopen`은 `kotlin-spring` 플러그인의 기반 라이브러리

**현재 상태**:
- buildSrc/build.gradle.kts에 `kotlin-allopen` 의존성이 포함되어 있음 (12-13행)
- example 모듈에서 `kotlin("plugin.spring")`을 사용 중 (6행)
- gradle/libs.versions.toml에 `kotlin-spring` 플러그인 정의됨 (18행)

**해결 방법**:
1. buildSrc에서 `kotlin-allopen` 의존성 제거 (Spring 모듈에서만 필요)
2. 또는 example 모듈에서 `alias(libs.plugins.kotlin.spring)` 대신 버전 없이 `kotlin("plugin.spring")` 사용

## Git 상태 (스냅샷)
- 현재 브랜치: main
- 수정된 파일:
  - .idea/gradle.xml
  - .idea/misc.xml
  - .idea/vcs.xml
  - buildSrc/build.gradle.kts
  - example/build.gradle.kts
- 추적되지 않는 파일:
  - gradle/libs.versions.toml

## 참고 문서
- [AGENTS.md](AGENTS.md): 상세한 가이드라인 (구조, 빌드, 코딩 스타일, 테스트, 커밋/PR)
