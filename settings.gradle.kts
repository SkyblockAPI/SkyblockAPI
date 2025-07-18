rootProject.name = "skyblock-api"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.msrandom.net/repository/cloche")
        maven(url = "https://maven.fabricmc.net/")
        mavenLocal()
    }
}

include(":annotations")
include(":compiler")
