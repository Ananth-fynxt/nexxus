plugins {
    id("build.library")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.actuator)
    implementation(libs.spring.boot.starter.opentelemetry)
    implementation(libs.datasource.micrometer.spring.boot)
    implementation(libs.opentelemetry.logback.appender)
}
