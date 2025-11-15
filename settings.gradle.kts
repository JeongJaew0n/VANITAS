// [Multi Moudle] 1. 루트 프로젝트와 하위 모듈을 Gradle에 등록한다.
rootProject.name = "vanitas" // 루트 프로젝트 이름 지정

pluginManagement {
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

include("example") // 기본 Spring Boot 서비스 모듈
