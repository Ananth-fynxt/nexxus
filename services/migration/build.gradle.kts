plugins {
    id("build.service")
}

dependencies {
    implementation(project(":libs:common"))
}

application {
    mainClass = "fynxt.migration.MigrationApplication"
}
