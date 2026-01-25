plugins {
    id("build.service")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.web)
    implementation(libs.jobrunr.spring.boot.starter)
    implementation(libs.jackson2.databind)
    implementation(project(":libs:database"))
    runtimeOnly(libs.postgresql)
}
