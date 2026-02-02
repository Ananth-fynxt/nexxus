plugins {
    id("build.common")
    application
}

dependencies {
    implementation(project(":libs:common"))
    implementation(project(":libs:mapper"))
    implementation(project(":libs:otel"))
}
