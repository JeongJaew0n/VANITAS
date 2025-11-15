// [Multi Moudle] 2. 루트에서 group/version을 정의해 모든 모듈이 동일한 좌표를 공유하게 한다.
group = "com.nhn.inje.ccp"
version = "0.0.1-SNAPSHOT"

subprojects {
    apply(plugin = "common") // Kotlin/JVM 및 ktlint 공통 컨벤션 적용

    group = rootProject.group
    version = rootProject.version
}
