plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    maven("https://maven.teamresourceful.com/repository/maven-public/")
}

dependencies {
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("me.owdding.repo:compacting-resources:1.0.0")
}
