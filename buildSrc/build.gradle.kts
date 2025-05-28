plugins {
    `kotlin-dsl`
    kotlin("jvm") version ("2.1.0")
    `java-gradle-plugin`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.teamresourceful.com/repository/maven-public/")
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:1.8.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:2.1.0")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:2.1.0")
    implementation("com.google.code.gson:gson:2.12.1")
    implementation("me.owdding.repo:compacting-resources:1.0.0")
}

gradlePlugin {
    plugins {
        create("removeNextVersion") {
            id = "remove-next-version"
            implementationClass = "tech.thatgravyboat.skyblockapi.item.RemoveNextVersionGradlePlugin"
        }
    }
}
