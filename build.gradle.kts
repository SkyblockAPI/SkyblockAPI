@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.task.ValidateAccessWidenerTask

plugins {
    id("net.fabricmc.fabric-loom")
    `sbapi-setup`
}

val mcVersion = stonecutter.current.version.replace(".", "")
val accessWidenerFile = rootProject.file("src/sbapi.accesswidener")

loom {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Dfabric.modsFolder=\"${mcVersion}Mods\"")
    }

    if (accessWidenerFile.exists()) {
        accessWidenerPath.set(accessWidenerFile)
    }
}

kotlin {
    jvmToolchain(25)
}

java {
    targetCompatibility = JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_21
}

tasks.withType<ValidateAccessWidenerTask> { enabled = false }


dependencies {
    val api = "api"
    val implementation = "implementation"
    val runtimeOnly = "runtimeOnly"

    "minecraft"(versionedCatalog["minecraft"])

    "compileOnly"(project(":annotations"))
    "compileOnly"(versionedCatalog.bundles["meowdding"])

    implementation(versionedCatalog["fabric.language.kotlin"])

    "api"(versionedCatalog["meowdding.item.dfu"]) {
        capabilities { requireCapability("me.owdding:item-data-fixer-${stonecutter.current.version}") }
    }
    "include"(versionedCatalog["meowdding.item.dfu"]) {
        capabilities {
            requireCapability("me.owdding:item-data-fixer-${stonecutter.current.version}")
        }
    }

    "api"(versionedCatalog["hypixel.modapi"])
    implementation(versionedCatalog.bundles["hypixel"])
    "include"(versionedCatalog["hypixel.modapi.fabric"])

    api(versionedCatalog["skyblockapi.repolib"])
    "include"(versionedCatalog["skyblockapi.repolib"])

    runtimeOnly(versionedCatalog["devauth"])

    implementation(versionedCatalog["fabric.api"])
    implementation(versionedCatalog["fabric.loader"])
}

tasks.withType<Jar> {
    archiveClassifier = stonecutter.current.version
}
