plugins {
    id("build.library")
}

dependencies {
    api(libs.spring.boot.starter.data.jpa)

    api(libs.hibernate.envers)
    implementation(libs.jackson.databind)
}
