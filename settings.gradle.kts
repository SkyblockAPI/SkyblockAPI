rootProject.name = "SkyblockAPI"

pluginManagement {
    repositories {
        gradlePluginPortal()
        maven(url = "https://maven.msrandom.net/repository/cloche")
        maven(url = "https://maven.fabricmc.net/")
    }
}

include(":annotations")
include(":compiler")
