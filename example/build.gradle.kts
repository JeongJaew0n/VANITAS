// [Multi Moudle] 6. 개별 모듈이 app 컨벤션 플러그인을 적용해 즉시 Spring Boot 구성을 상속한다.
plugins {
    // alias: Gradle version catalog(gradle/libs.versions.toml) 파일의 별명 참조한다는 뜻.
    alias(libs.plugins.spring.boot)
    alias(libs.plugins.spring.dependency.management)
    kotlin("plugin.spring")
}

dependencies {
    implementation(libs.spring.boot.starter.web)
    testImplementation(libs.spring.boot.starter.test)
}
