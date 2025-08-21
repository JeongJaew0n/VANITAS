rootProject.name = "demo"

pluginManagement {
    includeBuild("build-logic")
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

/**
 * Gradle이 의존성을 가져올 저장소 지정
 * Gradle 7.0 이상부터는 자동 기본 저장소 설정이 없음. 그래서 해당 설정이 없으면 외부 의존성을 가져올 수 없고, 플러그인도 다운로드 되지 않음.
 * 기본 Gradle Wrapper 파일이 있으면 로컬 캐시에 있는 의존성은 사용가능하지만, 새로 다운은 불가.
 */
dependencyResolutionManagement {
    repositories {
        gradlePluginPortal() // Gradle 플러그인을 다운 받는 저장소. plugins { id("...") version "..." } 형태로 적용하는 플로그인들이 여기서 받아짐.
        mavenCentral()
    }
}

plugins {
    /**
     * Gradle의 Java Tollchians 기능과 함께 동작.
     * Foojay API 를 사용해 필요한 JDK를 자동으로 다운로드하거나 설치 위치를 찾아줌.
     */
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}