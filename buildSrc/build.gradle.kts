// [Multi Moudle] 3. buildSrc를 통해 모든 모듈이 공유할 빌드 로직을 컴파일한다.
plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.0.21")
    implementation("org.jetbrains.kotlin:kotlin-allopen:2.0.21")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:3.5.4")
    implementation("io.spring.gradle:dependency-management-plugin:1.1.6")
    implementation("org.jlleitschuh.gradle:ktlint-gradle:12.1.1")
}
