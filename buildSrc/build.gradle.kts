plugins {
    alias(libs.plugins.kotlin.jvm) version (libs.versions.kotlin.asProvider())
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.teamresourceful.com/repository/maven-public/")
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.kotlin.gradle.plugin)
    implementation(libs.google.gson)
    implementation(libs.meowdding.resources)
}

gradlePlugin {
    plugins {
        create("removeNextVersion") {
            id = "remove-next-version"
            implementationClass = "tech.thatgravyboat.skyblockapi.item.RemoveNextVersionGradlePlugin"
        }
    }
}
