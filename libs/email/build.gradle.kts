plugins {
    id("build.library")
}

dependencies {
    implementation(platform(libs.spring.boot.dependencies))
    api(libs.spring.boot.starter.web)
    implementation(libs.azure.communication.services.email)
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
