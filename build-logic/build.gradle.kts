plugins {
    `kotlin-dsl` // Kotlin DSL로 컨벤션 플러그인을 작성
    `java-library` // 플러그인 코드에 필요한 Java/Kotlin API 제공
}

object Versions {
    const val KOTLIN = "2.1.21"
    const val SPRING_BOOT = "3.5.4"
    const val DEPENDENCY_MANAGEMENT = "1.1.7"
    const val KTLINT = "13.0.0"
}

dependencies {
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:${Versions.KOTLIN}") // kotlin("jvm")
    implementation("org.jetbrains.kotlin.plugin.spring:org.jetbrains.kotlin.plugin.spring.gradle.plugin:${Versions.KOTLIN}") // kotlin("plugin.spring")
    implementation("org.jetbrains.kotlin.plugin.allopen:org.jetbrains.kotlin.plugin.allopen.gradle.plugin:${Versions.KOTLIN}") // kotlin("plugin.allopen")
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${Versions.SPRING_BOOT}") // org.springframework.boot 플러그인 의존성
    implementation("io.spring.gradle:dependency-management-plugin:${Versions.DEPENDENCY_MANAGEMENT}") // io.spring.dependency-management 플러그인 의존성
    implementation("org.jlleitschuh.gradle:ktlint-gradle:${Versions.KTLINT}") // ktlint 플러그인 의존성
}
