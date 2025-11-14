rootProject.name = "vanitas" // 루트 프로젝트 이름 지정

pluginManagement {
    includeBuild("build-logic") // 사전 컴파일 컨벤션 플러그인을 프로젝트에 연결
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        gradlePluginPortal() // 플러그인 및 의존성 다운로드 저장소
        mavenCentral()
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0" // 필요한 JDK를 자동으로 준비
}

include("example") // Spring Boot 애플리케이션 모듈 등록
