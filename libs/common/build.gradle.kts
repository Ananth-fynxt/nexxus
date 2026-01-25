plugins {
    id("build.library")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    implementation(libs.spring.boot.starter.webmvc)
    implementation(libs.spring.data.commons)

    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
