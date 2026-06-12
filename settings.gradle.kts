pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.teamresourceful.com/repository/maven-public/")
        maven("https://maven.kikugie.dev/snapshots")
        maven("https://maven.fabricmc.net/")
    }
}


rootProject.name = "skyblock-api"

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
    id("dev.kikugie.stonecutter") version "0.9-beta.1"
}

val versions = listOf("26.2", "26.1")

stonecutter {
    create(rootProject) {
        versions.forEach {
            version(it).buildscript = "build.gradle.kts"
        }
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
