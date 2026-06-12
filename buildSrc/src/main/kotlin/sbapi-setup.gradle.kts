@file:OptIn(ExperimentalAbiValidation::class)

import com.google.devtools.ksp.gradle.KspExtension
import me.owdding.AutoMixinExtension
import me.owdding.repo.resources.CompactingResourcesExtension
import org.gradle.plugins.ide.idea.model.IdeaModel
import org.jetbrains.kotlin.gradle.dsl.KotlinProjectExtension
import org.jetbrains.kotlin.gradle.dsl.abi.ExperimentalAbiValidation
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm")
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

private val stonecutter = project.extensions.getByName("stonecutter") as dev.kikugie.stonecutter.build.StonecutterBuildExtension

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

fun isUnobfuscated() = stonecutter.eval(stonecutter.current.version, ">=26.1")

dependencies {
    "ksp"(versionedCatalog.bundles["meowdding"])
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
    //compilerOptions.jvmTarget.set(jvmTargetVersion)
    compilerOptions.optIn.add("kotlin.time.ExperimentalTime")
    compilerOptions.freeCompilerArgs.addAll(
        "-Xcontext-parameters",
        "-Xcontext-sensitive-resolution",
        "-Xnullability-annotations=@org.jspecify.annotations:ignore",
        "-Xcontext-parameters"
    )
}

val accessWidenerFile = rootProject.file("src/sbapi.accesswidener")

tasks.withType<ProcessResources>().configureEach {
    with(copySpec {
        from(accessWidenerFile)
    })
    filteringCharset = "UTF-8"
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

(extensions.getByName("base") as BasePluginExtension).apply {
    archivesName = archiveName
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

val javaVersion get() = if (isUnobfuscated()) 25 else 21

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(javaVersion)
}


extensions.getByName<JavaPluginExtension>("java").apply {
    toolchain.languageVersion = JavaLanguageVersion.of(javaVersion)
    withSourcesJar()
}

afterEvaluate {
    extensions.getByName<KotlinProjectExtension>("kotlin").apply {
        jvmToolchain(javaVersion)
    }

    if (!isUnobfuscated()) {
        tasks.named<AbstractArchiveTask>("remapJar") {
            archiveClassifier = stonecutter.current.version
        }
    }
}

extensions.getByType<KspExtension>().apply {
    arg("meowdding.modules.project_name", "SkyblockAPI")
    arg("meowdding.modules.package", "tech.thatgravyboat.skyblockapi.generated")
    arg("meowdding.codecs.project_name", "SkyblockAPI")
    arg("meowdding.codecs.package", "tech.thatgravyboat.skyblockapi.generated")
}

extensions.getByType<AutoMixinExtension>().apply {
    mixinPackage = "tech.thatgravyboat.skyblockapi.mixins"
    projectName = "skyblock-api"
    mixinExtrasVersion = "0.5.0"
}

extensions.getByType<IdeaModel>().apply {
    module {
        isDownloadJavadoc = true
        isDownloadSources = true

        excludeDirs.add(file("run"))
    }
}

extensions.getByType<CompactingResourcesExtension>().apply {
    basePath = "repo"
    pathDirectory = "../../src"

    configureTask(tasks.named<AbstractCopyTask>("processResources").get())

    removeComments("skyblockid/unobtainable_ids")
    substituteFromDifferentFile("slayer", "slayers")
}

kotlin {
    jvmToolchain(if (isUnobfuscated()) 25 else 21)
}

java {
    targetCompatibility = if (isUnobfuscated()) JavaVersion.VERSION_21 else JavaVersion.VERSION_25
    sourceCompatibility = JavaVersion.VERSION_21
}

dependencies {
    fun makeAlias(configuration: String) = if (isUnobfuscated()) configuration else "mod" + configuration.replaceFirstChar { it.uppercase() }

    val maybeModImplementation = makeAlias("implementation")
    val maybeModCompileOnly = makeAlias("compileOnly")
    val maybeModRuntimeOnly = makeAlias("runtimeOnly")
    val maybeModApi = makeAlias("api")

    "minecraft"(versionedCatalog["minecraft"])

    "compileOnly"(project(":annotations"))
    "compileOnly"(versionedCatalog.bundles["meowdding"])

    maybeModApi(versionedCatalog["fabric.language.kotlin"])

    maybeModApi(versionedCatalog["meowdding.item.dfu"]) {
        capabilities { requireCapability("me.owdding:item-data-fixer-${stonecutter.current.version}") }
    }
    "include"(versionedCatalog["meowdding.item.dfu"]) {
        capabilities {
            if (isUnobfuscated()) {
                requireCapability("me.owdding:item-data-fixer-${stonecutter.current.version}")
            } else {
                requireCapability("me.owdding:item-data-fixer-${stonecutter.current.version}-remapped")
            }
        }
    }

    "api"(versionedCatalog["hypixel.modapi"])
    (if (isUnobfuscated()) maybeModApi else maybeModImplementation)(versionedCatalog["hypixel.modapi.fabric"])
    "include"(versionedCatalog["hypixel.modapi.fabric"])

    maybeModApi(versionedCatalog["skyblockapi.repolib"])
    "include"(versionedCatalog["skyblockapi.repolib"])

    maybeModRuntimeOnly(versionedCatalog["devauth"])

    maybeModImplementation(versionedCatalog["fabric.api"])
    maybeModImplementation(versionedCatalog["fabric.loader"])
}

