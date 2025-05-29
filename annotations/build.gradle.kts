plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.binary.compatibility) apply false
}

kotlin {
    jvmToolchain(21)
}

repositories {
    mavenCentral()
}
