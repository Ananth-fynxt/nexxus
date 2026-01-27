plugins {
    id("build.library")
}

dependencies {
    api(project(":libs:common"))
    api(libs.aspectjrt)
    api(libs.spring.boot.starter.web)
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
