plugins {
    id("build.library")
}

dependencies {
    api(project(":libs:common"))
    implementation(platform(libs.spring.boot.dependencies))
    api(libs.spring.boot.starter.aop)
    api(libs.spring.boot.starter.web)
    implementation(libs.lombok)
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
}
