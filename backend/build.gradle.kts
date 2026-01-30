plugins {
    base
    alias(libs.plugins.spotless)
    alias(libs.plugins.version.catalog.update)
}

repositories {
    mavenCentral()
}

versionCatalogUpdate {
    sortByKey.set(false)

    keep {
        keepUnusedVersions.set(false)
    }
}

allprojects {
    apply(plugin = "com.diffplug.spotless")

    repositories {
        // mavenLocal()
        mavenCentral()
        maven {
            url = uri("https://pkgs.dev.azure.com/tech4jc/_packaging/fynxt-libs/maven/v1")
            name = "AzureArtifacts"
            credentials {
                username = findProperty("fynxt-libsUsername") as String?
                password = findProperty("fynxt-libsPassword") as String?
            }
            authentication {
                create("basic", org.gradle.authentication.http.BasicAuthentication::class.java)
            }
        }
    }

    spotless {
        java {
            target("**/src/*/java/**/*.java")
            targetExclude("**/build/**")
            palantirJavaFormat()
            importOrder("fynxt", "java", "javax", "*")
            formatAnnotations()
            leadingSpacesToTabs()
            removeUnusedImports()
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlin {
            target("buildSrc/**/*.kt")
            targetExclude("**/build/**")
            ktlint()
            trimTrailingWhitespace()
            endWithNewline()
        }

        kotlinGradle {
            target("*.gradle.kts", "buildSrc/**/*.gradle.kts")
            targetExclude("**/build/**")
            ktlint()
        }
    }
}
