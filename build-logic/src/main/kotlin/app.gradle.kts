import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    kotlin("plugin.allopen")
    kotlin("plugin.spring")
}

val catalog = extensions.findByType<VersionCatalogsExtension>()?.named("libs")

dependencies {
    catalog?.let {
        add("implementation", it.findLibrary("kotlin-stdlib").get())
        add("implementation", it.findLibrary("kotlin-reflect").get())
        add("implementation", it.findLibrary("spring-boot-starter").get())
        add("implementation", it.findLibrary("spring-boot-starter-web").get())
    }
}


tasks {
    named<BootJar>("bootJar").configure {
        enabled = true
    }

    named<Jar>("jar").configure {
        enabled = false
    }
}
