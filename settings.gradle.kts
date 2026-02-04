rootProject.name = "skyblock-api"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    id("dev.kikugie.stonecutter") version "0.8.3"
}

val versions = listOf("26.1", "1.21.11", "1.21.10")

stonecutter {
    create(rootProject) {
        versions(versions)
        vcsVersion = versions.first()
    }
}


dependencyResolutionManagement {
    versionCatalogs {
        versions.forEach {
            val name = it.replace(".", "")
            create("libs$name") {
                from(
                    files(
                        rootProject.projectDir.resolve("gradle/${it.replace(".", "_")}.versions.toml")
                    )
                )
            }
        }
    }
}

include(":annotations")
