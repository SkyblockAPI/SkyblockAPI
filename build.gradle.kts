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

tasks.withType<ValidateAccessWidenerTask> { enabled = false }

tasks.named<Jar>("jar") {
    archiveClassifier = stonecutter.current.version
}

tasks.named<Jar>("sourcesJar") {
    archiveClassifier = "${stonecutter.current.version}-sources"
}
