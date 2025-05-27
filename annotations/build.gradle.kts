plugins {
    idea
    kotlin("jvm")
    java
}

java.toolchain.languageVersion = JavaLanguageVersion.of(21)

repositories {
    mavenCentral()
    maven("https://maven.teamresourceful.com/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("com.google.devtools.ksp:symbol-processing-api:2.0.20-1.0.25")

    implementation("com.google.code.gson:gson:2.13.1")

    implementation("me.owdding.kotlinpoet:kotlinpoet-jvm:2.2.0-SNAPSHOT")
    implementation("me.owdding.kotlinpoet:ksp:2.2.0-SNAPSHOT")
}
