// [Multi Moudle] 3. buildSrc를 통해 모든 모듈이 공유할 빌드 로직을 컴파일한다.
plugins {
    `kotlin-dsl`
}

dependencies {
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.kotlin.allopen)
    implementation(libs.ktlint.gradle)
}
