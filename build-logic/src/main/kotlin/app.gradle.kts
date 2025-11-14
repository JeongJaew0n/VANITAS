import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot") // Spring Boot 플러그인 적용
    id("io.spring.dependency-management") // Spring BOM 의존성 관리를 활성화
    kotlin("plugin.allopen") // Spring 프록시에 필요한 all-open 변환
    kotlin("plugin.spring") // Spring 친화적인 Kotlin 컴파일러 확장
}

val libs = extensions.getByType<VersionCatalogsExtension>().named("libs") // 버전 카탈로그 참조

dependencies {
    add("implementation", libs.findLibrary("kotlin-stdlib").get()) // Kotlin 표준 라이브러리
    add("implementation", libs.findLibrary("kotlin-reflect").get()) // Kotlin 리플렉션 지원
    add("implementation", libs.findLibrary("spring-boot-starter").get()) // Spring Boot 기본 스타터
    add("implementation", libs.findLibrary("spring-boot-starter-web").get()) // Spring MVC/REST 스타터
}

tasks.named<BootJar>("bootJar").configure {
    enabled = true // 실행 가능한 Boot JAR 생성
}

tasks.named<Jar>("jar").configure {
    enabled = false // 일반 JAR 비활성화로 중복 패키징 방지
}
