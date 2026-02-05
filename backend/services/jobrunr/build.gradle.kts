plugins {
    id("build.service")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.jackson2.databind)
    implementation(project(":libs:jobrunr"))
    implementation(project(":libs:database"))
    runtimeOnly(libs.postgresql)
}
