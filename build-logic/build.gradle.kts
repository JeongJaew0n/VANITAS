plugins {
    `kotlin-dsl` // precompiled script plugin 를 사용할 수 있게 해줌.
    `java-library` // api, implementation 와 같은 의존성 구성을 사용할 수 있게 해줌. api: 외부 노출, implementation: 내부 사용. 현재 script에서는 api가 없으니 지워도 무방.
}

object Versions {
    const val KOTLIN = "2.1.21"
    const val SPRING_BOOT = "3.5.4"
    const val DEPENDENCY_MANAGEMENT = "1.1.7"
    const val KTLINT = "13.0.0"
}

// plugins
dependencies {
    // Kotlin Gradle
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:${Versions.KOTLIN}")
    implementation("org.jetbrains.kotlin.plugin.spring:org.jetbrains.kotlin.plugin.spring.gradle.plugin:${Versions.KOTLIN}")
    implementation("org.jetbrains.kotlin.plugin.jpa:org.jetbrains.kotlin.plugin.jpa.gradle.plugin:${Versions.KOTLIN}")
    implementation("org.jetbrains.kotlin.plugin.allopen:org.jetbrains.kotlin.plugin.allopen.gradle.plugin:${Versions.KOTLIN}")

    // Spring Boot
    implementation("org.springframework.boot:spring-boot-gradle-plugin:${Versions.SPRING_BOOT}")

    // Dependency Management
    implementation("io.spring.gradle:dependency-management-plugin:${Versions.DEPENDENCY_MANAGEMENT}")

    // ktlint
    implementation("org.jlleitschuh.gradle:ktlint-gradle:${Versions.KTLINT}")
}
