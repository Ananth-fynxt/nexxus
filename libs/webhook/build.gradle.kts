plugins {
    id("build.library")
}

dependencies {
    api(libs.spring.boot.starter.web)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
