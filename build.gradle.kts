
plugins {
    idea
    `versioned-catalogues`
    `sbapi-setup`
    `item-data`
    alias(libs.plugins.kotlin.symbol.processor)
    alias(libs.plugins.meowdding.resources)
    alias(libs.plugins.meowdding.auto.mixins)
    kotlin("jvm")
    id("net.fabricmc.fabric-loom")
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(25)
    withSourcesJar()
}
