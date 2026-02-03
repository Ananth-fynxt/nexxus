plugins {
    id("build.library")
}

dependencies {
    // Spring Boot BOM for version management
    implementation(platform(libs.spring.boot.dependencies))

    // Common library for shared constants, DTOs, utilities
    api(project(":libs:common"))

    // Spring Boot starters - only web and security (NO JPA/Database)
    api(libs.spring.boot.starter.web)
    api("org.springframework.boot:spring-boot-starter-security")
    api("org.springframework.boot:spring-boot-starter-validation")

    // Jackson for JSON processing
    api(libs.jackson2.databind)
    api(libs.jackson2.datatype.jsr310)

    // Commons Lang for utilities
    implementation("org.apache.commons:commons-lang3")

    // Development Tools
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)

    // Swagger/OpenAPI Documentation
    api(libs.springdoc.openapi.starter.webmvc.ui)

    // JWT Processing (from Azure Artifacts)
    implementation(libs.jwt)
}
