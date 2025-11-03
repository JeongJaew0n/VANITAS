plugins {
    kotlin("jvm")
}

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test> { useJUnitPlatform() }
