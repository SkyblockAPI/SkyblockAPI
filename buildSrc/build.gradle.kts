plugins {
    `kotlin-dsl`
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
    implementation("dev.kikugie.stonecutter:dev.kikugie.stonecutter.gradle.plugin:0.8.3")
}

gradlePlugin {
    plugins {
        create("removeNextVersion") {
            id = "remove-next-version"
            implementationClass = "tech.thatgravyboat.skyblockapi.item.RemoveNextVersionGradlePlugin"
        }
    }
}
