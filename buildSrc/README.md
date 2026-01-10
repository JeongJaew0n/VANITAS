# buildSrc 디렉토리

## 개요
`buildSrc`는 Gradle의 특수한 디렉토리로, **프로젝트 전체에서 공유할 빌드 로직을 정의하는 곳**입니다.
이 디렉토리의 코드는 실제 프로젝트 빌드 전에 자동으로 컴파일되며, 모든 하위 모듈에서 재사용할 수 있는 컨벤션 플러그인을 제공합니다.

## 역할
1. **중앙 집중식 빌드 로직**: 반복되는 Gradle 설정을 한 곳에서 관리
2. **컨벤션 플러그인 제공**: 각 모듈이 일관된 빌드 설정을 상속받도록 함
3. **타입 세이프**: Kotlin DSL로 작성되어 IDE 지원 및 컴파일 타임 검증 가능

## 디렉토리 구조

```
buildSrc/
├── build.gradle.kts              # buildSrc 자체의 빌드 설정
├── settings.gradle.kts           # buildSrc 전용 설정 (버전 카탈로그 참조)
└── src/main/kotlin/              # 컨벤션 플러그인 정의
    ├── jvm.gradle.kts            # Kotlin/JVM 공통 컨벤션
    └── ktlint.gradle.kts         # ktlint 공통 컨벤션
```

## 파일별 설명

### build.gradle.kts
buildSrc 자체를 빌드하기 위한 설정 파일입니다.

**주요 내용:**
- `kotlin-dsl` 플러그인 적용: Kotlin DSL로 Gradle 스크립트 작성 가능
- **의존성**:
  - `kotlin-gradle-plugin`: Kotlin 컴파일 플러그인
  - `kotlin-allopen`: Kotlin의 all-open 플러그인 (현재 이슈 원인)
  - `ktlint-gradle`: 코드 스타일 검사 플러그인

**⚠️ 알려진 이슈:**
- `kotlin-allopen` 의존성이 하위 모듈의 `kotlin-spring` 플러그인과 충돌
- `kotlin-allopen`은 `kotlin-spring`의 기반 라이브러리이므로 중복 포함 시 버전 충돌 발생

### settings.gradle.kts
buildSrc 전용 설정 파일로, 상위 프로젝트의 버전 카탈로그를 참조합니다.

**주요 내용:**
- 저장소 설정: `gradlePluginPortal()`, `mavenCentral()`
- 버전 카탈로그 참조: `../gradle/libs.versions.toml`을 `libs`로 사용
- buildSrc에서도 상위 프로젝트의 버전 관리를 공유할 수 있도록 함

### src/main/kotlin/jvm.gradle.kts
**Kotlin/JVM 공통 컨벤션 플러그인**

모든 모듈이 공통으로 따를 Kotlin/JVM 빌드 설정을 정의합니다.

**적용 내용:**
- `kotlin("jvm")` 플러그인 적용
- **JVM 설정**:
  - JVM Toolchain: 21
  - JVM Target: JVM_21
  - Compiler Args: `-Xjsr305=strict` (null-safety 강화)
- **테스트 설정**:
  - JUnit Platform 사용 (`useJUnitPlatform()`)
- **저장소**: mavenCentral()

**사용 방법:**
```kotlin
// 하위 모듈의 build.gradle.kts에서
apply(plugin = "jvm")
```

### src/main/kotlin/ktlint.gradle.kts
**ktlint 공통 컨벤션 플러그인**

모든 모듈에 코드 스타일 검사를 적용합니다.

**적용 내용:**
- `org.jlleitschuh.gradle.ktlint` 플러그인 적용
- 버전: 12.1.1 (libs.versions.toml에서 관리)

**사용 방법:**
```kotlin
// 하위 모듈의 build.gradle.kts에서
apply(plugin = "ktlint")
```

**제공 명령어:**
- `./gradlew ktlintCheck`: 코드 스타일 검사
- `./gradlew ktlintFormat`: 코드 스타일 자동 정렬

## 컨벤션 플러그인 적용 흐름

1. **buildSrc 컴파일**: Gradle이 프로젝트 빌드 전에 buildSrc를 먼저 컴파일
2. **플러그인 등록**: `jvm.gradle.kts`, `ktlint.gradle.kts`가 프리컴파일된 스크립트 플러그인으로 등록
3. **루트 프로젝트 적용**: `build.gradle.kts`의 `subprojects` 블록에서 모든 하위 모듈에 적용
   ```kotlin
   subprojects {
       apply(plugin = "jvm")
       apply(plugin = "ktlint")
   }
   ```
4. **개별 모듈 상속**: `example` 등 각 모듈이 자동으로 컨벤션 상속

## 장점

### 1. 코드 재사용
동일한 빌드 로직을 여러 모듈에서 반복하지 않음

### 2. 일관성 유지
모든 모듈이 동일한 JVM 버전, 컴파일 옵션, 코드 스타일 사용

### 3. 유지보수 용이
설정 변경 시 buildSrc만 수정하면 모든 모듈에 자동 반영

### 4. 타입 세이프
Kotlin DSL 사용으로 IDE 자동완성 및 컴파일 타임 검증

## 주의사항

1. **buildSrc 변경 시 전체 리빌드**: buildSrc 코드 수정 시 프로젝트 전체가 리빌드됨
2. **의존성 충돌 주의**: buildSrc에 추가한 의존성이 하위 모듈과 충돌할 수 있음
   - 현재 `kotlin-allopen` 이슈가 대표적인 예시
3. **버전 관리**: 가능한 `gradle/libs.versions.toml`에서 중앙 관리
4. **최소한의 의존성**: buildSrc에는 필수 빌드 도구만 포함

## 확장 방법

새로운 컨벤션 플러그인을 추가하려면:

1. `buildSrc/src/main/kotlin/` 아래에 새 `.gradle.kts` 파일 생성
   ```kotlin
   // my-convention.gradle.kts
   plugins {
       // 필요한 플러그인
   }

   // 공통 설정
   ```

2. 루트 `build.gradle.kts` 또는 개별 모듈에서 적용
   ```kotlin
   apply(plugin = "my-convention")
   ```

## 참고 문서
- [Gradle buildSrc 공식 문서](https://docs.gradle.org/current/userguide/organizing_gradle_projects.html#sec:build_sources)
- [Sharing Build Logic](https://docs.gradle.org/current/userguide/sharing_build_logic_between_subprojects.html)
