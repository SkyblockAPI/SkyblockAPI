@file:Suppress("UnstableApiUsage")

import net.fabricmc.loom.task.RemapJarTask
import net.fabricmc.loom.task.ValidateAccessWidenerTask

plugins {
    id("net.fabricmc.fabric-loom-remap")
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

dependencies {
    mappings(loom.layered {
        officialMojangMappings()
        parchment(variantOf(versionedCatalog["parchment"]) {
            artifactType("zip")
        })
    })
}

tasks.withType<ValidateAccessWidenerTask> { enabled = false }


tasks.named<Jar>("jar") {
    archiveClassifier = stonecutter.current.version
}

tasks.named<RemapJarTask>("remapJar") {
    archiveClassifier = "${stonecutter.current.version}-remapped"
}

tasks.named<Jar>("sourcesJar") {
    archiveClassifier = "${stonecutter.current.version}-sources"
}
