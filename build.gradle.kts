@file:OptIn(ExperimentalAbiValidation::class)

import me.owdding.repo.resources.CompactingResourcesExtension
import net.fabricmc.loom.task.ValidateAccessWidenerTask
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
    id("net.fabricmc.fabric-loom")
    id("me.owdding.resources")
    id("me.owdding.auto-mixins")
    id("com.google.devtools.ksp")
    id("versioned-catalogues")
    id("item-data")
    id("idea")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.add("-Xrender-internal-diagnostic-names")
    }

    abiValidation {
        enabled = true

        filters {
            excluded {
                byNames.addAll(
                    "tech.thatgrabyboat.skyblockapi.impl.**",
                    "tech.thatgravyboat.skyblockapi.mixins.**"
                )
            }
        }
    }
}

repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven(
        "https://maven.teamresourceful.com/repository/maven-public/",
        "earth.terrarium",
        "com.teamresourceful",
        "tech.thatgravyboat",
        "me.owdding",
        "com.terraformersmc",
        "net.hypixel"
    )
    //scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.nucleoid.xyz/", "eu.pb4")
    mavenCentral()
    mavenLocal()
}

val archiveName = "skyblock-api"

tasks.named("build") {
    doLast {
        val sourceFile = rootProject.projectDir.resolve("versions/${project.name}/build/libs/${archiveName}-$version-${stonecutter.current.version}.jar")
        val targetFile = rootProject.projectDir.resolve("build/libs/${archiveName}-$version-${stonecutter.current.version}.jar")
        targetFile.parentFile.mkdirs()
        targetFile.writeBytes(sourceFile.readBytes())
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    compilerOptions.freeCompilerArgs.addAll(
        "-Xcontext-parameters",
        "-Xcontext-sensitive-resolution",
        "-Xnullability-annotations=@org.jspecify.annotations:ignore",
        "-Xcontext-parameters"
    )
}

val accessWidenerFile = rootProject.file("src/sbapi.classtweaker")

tasks.withType<ProcessResources>().configureEach {
    with(copySpec {
        from(accessWidenerFile)
    })
    filteringCharset = "UTF-8"
}

afterEvaluate {
    loom.apply {
        log4jConfigs.removeAll { true }
        log4jConfigs.from(rootProject.layout.projectDirectory.file("gradle/log4j.config.xml"))
    }
}

tasks.named<ProcessResources>("processResources") {
    val range = if (versionedCatalog.versions.has("minecraft.range")) {
        versionedCatalog.versions.get("minecraft.range").toString()
    } else {
        val start = versionedCatalog.versions.getOrFallback("minecraft.start", "minecraft")
        val end = versionedCatalog.versions.getOrFallback("minecraft.end", "minecraft")
        ">=$start <=$end"
    }

    val replacements = mapOf(
        "version" to project.version,
        "minecraft_range" to range,
        "fabric_lang_kotlin" to versionedCatalog.versions.get("fabric.language.kotlin"),
        "hypixel_mod_api" to versionedCatalog.versions.get("hypixel.modapi.fabric"),
        "fabric_loader" to versionedCatalog.versions.get("fabric.loader"),
    )
    inputs.properties(replacements)

    filesMatching("fabric.mod.json") {
        expand(replacements)
    }
}

base {
    archivesName = archiveName
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val javaVersion get() = 25

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}


java {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

kotlin {
    jvmToolchain(javaVersion)
}


ksp {
    arg("meowdding.modules.project_name", "SkyblockAPI")
    arg("meowdding.modules.package", "tech.thatgravyboat.skyblockapi.generated")
    arg("meowdding.codecs.project_name", "SkyblockAPI")
    arg("meowdding.codecs.package", "tech.thatgravyboat.skyblockapi.generated")
}

autoMixins {
    mixinPackage = "tech.thatgravyboat.skyblockapi.mixins"
    projectName = "skyblock-api"
    mixinExtrasVersion = "0.5.0"
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}

compactingResources {
    basePath = "repo"
    pathDirectory = "../../src"

    configureTask(tasks.named<AbstractCopyTask>("processResources").get())

    removeComments("skyblockid/unobtainable_ids")
    substituteFromDifferentFile("slayer", "slayers")
}

kotlin {
    jvmToolchain(25)
}

java {
    targetCompatibility = JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_25
}

dependencies {
    minecraft(versionedCatalog["minecraft"])

    compileOnly(project(":annotations"))
    compileOnly(versionedCatalog.bundles["meowdding"])
    ksp(versionedCatalog.bundles["meowdding"])

    api(versionedCatalog["fabric.language.kotlin"])

    api(versionedCatalog["meowdding.item.dfu"]) {
        capabilities { requireCapability("me.owdding:item-data-fixer-${stonecutter.current.version}") }
    }
    include(versionedCatalog["meowdding.item.dfu"]) {
        capabilities { requireCapability("me.owdding:item-data-fixer-${stonecutter.current.version}") }
    }

    api(versionedCatalog["hypixel.modapi"])
    api(versionedCatalog["hypixel.modapi.fabric"])
    include(versionedCatalog["hypixel.modapi.fabric"])

    api(versionedCatalog["skyblockapi.repolib"])
    include(versionedCatalog["skyblockapi.repolib"])

    runtimeOnly(versionedCatalog["devauth"])

    implementation(versionedCatalog["fabric.api"])
    implementation(versionedCatalog["fabric.loader"])
}

val mcVersion = stonecutter.current.version.replace(".", "")

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
