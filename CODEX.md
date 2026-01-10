# Codex Agent Notes

Use this file as a short project map when analyzing or generating code.

## Project overview
- Gradle Kotlin DSL multi-module repo with a single module: `example`.
- Main Spring Boot entry point: `example/src/main/kotlin/com/nhn/inje/ccp/Main.kt`.
- Outputs: module `build/` folders and root `build/reports`.

## Modules and build logic
- `example`: Spring Boot app module.
- Shared build logic lives in `buildSrc`:
  - `buildSrc/src/main/kotlin/jvm.gradle.kts` (Kotlin/JVM conventions)
  - `buildSrc/src/main/kotlin/ktlint.gradle.kts` (ktlint conventions)

## Version catalog
- Central versions and coordinates: `gradle/libs.versions.toml`.
- Root and buildSrc both load the catalog (see `buildSrc/settings.gradle.kts`).
- Use `alias(libs.plugins.*)` and `libs.*` instead of inline versions.

## Commands
- Build all: `./gradlew clean build`
- Run app: `./gradlew :example:bootRun`
- Test module: `./gradlew :example:test`
- Lint: `./gradlew ktlintCheck` / `./gradlew ktlintFormat`

## Conventions
- Kotlin: 4-space indent, prefer `val`, expression body for simple returns.
- Package path matches `com.nhn.inje.ccp.<feature>`.
- Tests: JUnit 5, class `<Subject>Test`, method `shouldReturn...`.

## Notes
- `kotlin("plugin.spring")` is applied without a version to avoid classpath
  conflicts caused by Kotlin plugins loaded via buildSrc.
