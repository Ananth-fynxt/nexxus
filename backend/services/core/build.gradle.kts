plugins {
    id("build.service")
    id("build.database")
    alias(libs.plugins.spring.boot)
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.boot.starter.data.jpa)
    implementation(libs.spring.boot.starter.jdbc)
    implementation(libs.postgresql)
    implementation(libs.flyway.core)
    implementation(libs.flyway.postgresql)
    implementation(libs.flow)
    implementation(libs.denovm)
    implementation(libs.jwt)
    implementation(libs.jackson2.databind)
    implementation(libs.jackson2.datatype.jsr310)
    implementation(libs.springdoc.openapi.starter.webmvc.ui)
}
