plugins {
    kotlin("jvm") // Kotlin/JVM 플러그인 적용
    id("org.jlleitschuh.gradle.ktlint") // Kotlin 코드 스타일 검사 플러그인 적용
}

java {
    sourceCompatibility = JavaVersion.VERSION_21 // 자바 21 바이트코드 타깃
}

kotlin {
    jvmToolchain(21) // Gradle Toolchain으로 JDK 21 구성
}

tasks.withType<Test> {
    useJUnitPlatform() // JUnit 5 기반 테스트 실행
}
