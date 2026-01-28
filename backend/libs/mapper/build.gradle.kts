plugins {
    id("build.library")
}

dependencies {
    api(libs.mapstruct)

    annotationProcessor(libs.mapstruct.processor)

    implementation(libs.jackson.databind)
}
