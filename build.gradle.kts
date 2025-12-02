@file:Suppress("UnstableApiUsage")

import com.google.devtools.ksp.gradle.KspAATask
import net.fabricmc.loom.task.ValidateAccessWidenerTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    idea
    id("fabric-loom")
    kotlin("jvm")
    alias(libs.plugins.kotlin.symbol.processor)
    alias(libs.plugins.meowdding.resources)
    alias(libs.plugins.meowdding.auto.mixins)
    //alias(libs.plugins.detekt)
    `versioned-catalogues`
    `item-data`
}

repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven(
        "https://maven.teamresourceful.com/repository/maven-public/",
        "earth.terrarium",
        "com.teamresourceful",
        "tech.thatgravyboat",
        "me.owdding",
        "com.terraformersmc"
    )
    scopedMaven("https://maven.nucleoid.xyz/", "eu.pb4")
    mavenCentral()
}
dependencies {
    minecraft(versionedCatalog["minecraft"])
    mappings(loom.layered {
        officialMojangMappings()
        parchment(variantOf(versionedCatalog["parchment"]) {
            artifactType("zip")
        })
    })

    compileOnly(project(":annotations"))
    modCompileOnly(libs.bundles.meowdding)
    modCompileOnlyApi(libs.bundles.meowdding)

    modApi(variantOf(libs.meowdding.item.dfu) {
        classifier(stonecutter.current.version)
    })
    modImplementation(libs.fabric.language.kotlin)
    modApi(libs.bundles.hypixel)
    modApi(libs.skyblockapi.repolib)

    //includeImplementation(versionedCatalog["resourceful.config"])
    //includeImplementation(versionedCatalog["resourceful.lib"])
    //includeImplementation(versionedCatalog["placeholders"])
    //includeImplementation(versionedCatalog["placeholders"])
    //includeImplementation(libs.resourceful.config.kotlin)
    //includeImplementation(versionedCatalog["olympus"])
    //includeImplementation(libs.meowdding.remote.repo)
    //includeImplementation(libs.meowdding.lib)
    //includeImplementation(libs.skyblockapi)

    ksp(libs.meowdding.modules)
    ksp(libs.meowdding.ktcodecs)
    compileOnly(libs.meowdding.ktcodecs)
    compileOnly(libs.meowdding.ktcodecs)

    modImplementation(versionedCatalog["fabric.api"])
    modImplementation(libs.fabric.language.kotlin)
    modImplementation(libs.fabric.loader)
}

fun DependencyHandler.includeImplementation(dep: Any) {
    include(dep)
    modImplementation(dep)
}

val mcVersion = stonecutter.current.version.replace(".", "")
val accessWidenerFile = rootProject.file("src/sbapi.accesswidener")
loom {
    runConfigs["client"].apply {
        ideConfigGenerated(true)
        runDir = "../../run"
        vmArg("-Dfabric.modsFolder=" + '"' + "${mcVersion}Mods" + '"')
    }

    if (accessWidenerFile.exists()) {
        accessWidenerPath.set(accessWidenerFile)
    }
}

afterEvaluate {

    tasks.withType(KspAATask::class.java).configureEach {
        kspConfig.processorOptions.put(
            "meowdding.project_name",
            "SkyBlockAPI" + (kspConfig.cachesDir.get().asFile.name.takeUnless { it == "main" }?.let { "Versioned" } ?: "")
        )
    }
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

val archiveName = "skyblock-api"

base {
    archivesName.set("$archiveName-${archivesName.get()}")
}

tasks.named("build") {
    doLast {
        val sourceFile = rootProject.projectDir.resolve("versions/${project.name}/build/libs/${archiveName}-${stonecutter.current.version}-$version.jar")
        val targetFile = rootProject.projectDir.resolve("build/libs/${archiveName}-$version-${stonecutter.current.version}.jar")
        targetFile.parentFile.mkdirs()
        targetFile.writeBytes(sourceFile.readBytes())
    }
}

compactingResources {
    basePath = "repo"
    pathDirectory = "../../src"

    configureTask(tasks.named<AbstractCopyTask>("processResources").get())

    removeComments("skyblockid/unobtainable_ids")
    substituteFromDifferentFile("slayer", "slayers")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    compilerOptions.freeCompilerArgs.addAll(
        "-Xcontext-parameters",
        "-Xcontext-sensitive-resolution"
    )
}

tasks.processResources {
    val replacements = mapOf(
        "version" to version,
        "minecraft_start" to versionedCatalog.versions.getOrFallback("minecraft.start", "minecraft"),
        "minecraft_end" to versionedCatalog.versions.getOrFallback("minecraft.end", "minecraft"),
        //"fabric_lang_kotlin" to libs.versions.fabric.language.kotlin.get(),
        //"sbapi" to libs.versions.skyblockapi.asProvider().get(),
        //"rlib" to versionedCatalog.versions["resourceful.lib"],
        //"olympus" to versionedCatalog.versions["olympus"],
        //"mlib" to libs.versions.meowdding.lib.get(),
        //"rconfigkt" to libs.versions.rconfigkt.get(),
        //"rconfig" to versionedCatalog.versions["resourceful.config"],
        //"placeholder_api" to versionedCatalog.versions["placeholders"]
    )
    inputs.properties(replacements)

    filesMatching("fabric.mod.json") {
        expand(replacements)
    }
}

tasks.withType<ProcessResources>().configureEach {
    with(copySpec {
        from(accessWidenerFile)
    })
}

idea {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}

tasks.withType<ValidateAccessWidenerTask> { enabled = false }

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

tasks.processResources {
    filteringCharset = "UTF-8"
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
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
}
