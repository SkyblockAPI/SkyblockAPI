@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalPathApi::class)

import com.google.devtools.ksp.gradle.KspTask
import earth.terrarium.cloche.api.metadata.FabricMetadata
import earth.terrarium.cloche.api.metadata.ModMetadata
import net.msrandom.minecraftcodev.core.utils.toPath
import net.msrandom.minecraftcodev.fabric.task.JarInJar
import net.msrandom.minecraftcodev.runs.task.WriteClasspathFile
import net.msrandom.stubs.GenerateStubApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tech.thatgravyboat.skyblockapi.item.deprecationMessage
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.util.zip.ZipFile
import kotlin.io.path.*

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.terrarium.cloche)
    alias(libs.plugins.kotlin.symbol.processor)
    alias(libs.plugins.kotlin.binary.compatibility)
    alias(libs.plugins.meowdding.resources)
    `maven-publish`
    `remove-next-version`
    `item-data`
}

deprecationMessage = "This will be removed with the next minecraft version (1.21.6/1.22). Consider migrating to the new api before it is removed!"

base {
    archivesName.set(project.property("archives_base_name") as String)
}

java {
    toolchain.languageVersion = JavaLanguageVersion.of(21)
    withSourcesJar()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_2
        freeCompilerArgs.addAll(
            "-Xmulti-platform",
            "-Xno-check-actual",
            "-Xexpect-actual-classes",
            "-Xopt-in=kotlin.time.ExperimentalTime",
        )
    }
}


val compilerAll: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = true
}

val kspAll: Configuration by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = true
}

dependencies {
    ksp(libs.bundles.meowdding)
    compileOnly(project(":annotations"))
    compilerAll(rootProject.project(":compiler"))
    implementation(kotlin("stdlib-jdk8"))

    compileOnly(libs.bundles.meowdding)
    configurations.forEach {
        if (it.name.startsWith("kotlinCompilerPluginClasspath")) {
            compilerAll.allDependencies.forEach { dependency -> add(it.name, dependency) }
        } else if (it.name.startsWith("ksp") && !it.name.contains("classpath", true) && !it.name.contains("all", true)) {
            kspAll.allDependencies.forEach { dependency -> add(it.name, dependency) }
        }
    }
}

cloche {
    metadata {
        modId = "skyblock-api"
        name = "skyblock-api"
        license = "MIT"
        icon = "assets/skyblockapi/icon.png"
        clientOnly = true

        custom("modmenu" to mapOf("badges" to listOf("library")))
    }

    common {
        withPublication()

        dependencies {
            compileOnly(project(":annotations"))
            compileOnly.bundle(libs.bundles.meowdding)
            compileOnlyApi.bundle(libs.bundles.meowdding)

            implementation(libs.meowdding.item.dfu)
            runtimeOnly(libs.fabric.language.kotlin) {
                isTransitive = false
            }
            implementation.bundle(libs.bundles.hypixel)
            implementation(libs.skyblockapi.repolib)

            runtimeOnly(libs.devauth)
        }
    }

    fun createVersion(
        name: String,
        version: String = name,
        loaderVersion: Provider<String> = libs.versions.fabric.loader,
        fabricApiVersion: Provider<String> = libs.versions.fabric.api,
        minecraftVersionRange: ModMetadata.VersionRange.() -> Unit = {
            start = version
            end = version
            endExclusive = false
        },
    ) {
        fabric(name) {
            includedClient()
            minecraftVersion = version
            this.loaderVersion = loaderVersion.get()

            include(libs.skyblockapi.repolib)
            include(libs.hypixel.modapi.fabric)
            include(libs.meowdding.item.dfu)

            metadata {
                entrypoint("client", "tech.thatgravyboat.skyblockapi.api.SkyBlockAPI::postInit")
                entrypoint(
                    "main",
                    listOf(
                        "tech.thatgravyboat.skyblockapi.utils.regex.Regexes::load",
                        "tech.thatgravyboat.skyblockapi.api.SkyBlockAPI::init"
                    ).map { entrypoint ->
                        Action<FabricMetadata.Entrypoint> {
                            this.value.set(entrypoint)
                        }
                    }
                )


                fun dependency(modId: String, version: Provider<String>? = null) {
                    dependency {
                        this.modId = modId
                        this.required = true
                        if (version != null) version {
                            this.start = version
                        }
                    }
                }

                dependency("fabric-language-kotlin")
                dependency("fabric")
                dependency("fabricloader", loaderVersion)
                dependency("hypixel-mod-api", libs.versions.hypixel.modapi.fabric)
                dependency {
                    modId = "minecraft"
                    required = true
                    version(minecraftVersionRange)
                }
            }

            dependencies {
                fabricApi(fabricApiVersion.get(), minecraftVersion)

                val mods = project.layout.buildDirectory.get().toPath().resolve("tmp/extracted${sourceSet.name}RuntimeMods")
                val modsTmp = project.layout.buildDirectory.get().toPath().resolve("tmp/extracted${sourceSet.name}RuntimeMods/tmp")

                mods.deleteRecursively()
                modsTmp.createDirectories()
                mods.createDirectories()

                fun extractMods(file: Path) {
                    println("Adding runtime mod ${file.name}")
                    val extracted = mods.resolve(file.name)
                    file.copyTo(extracted, overwrite = true)
                    modRuntimeOnly(files(extracted))
                    ZipFile(extracted.toFile()).use {
                        it.entries().asIterator().forEach { file ->
                            val name = file.name.replace(File.separator, "/")
                            if (name.startsWith("META-INF/jars/") && name.endsWith(".jar")) {
                                val data = it.getInputStream(file).readAllBytes()
                                val file = modsTmp.resolve(name.substringAfterLast("/"))
                                file.writeBytes(data, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE)
                                extractMods(file)
                            }
                        }
                    }
                }

                project.layout.projectDirectory.toPath().resolve("run/${sourceSet.name}Mods").takeIf { it.exists() }
                    ?.listDirectoryEntries()?.filter { it.isRegularFile() }?.forEach { file ->
                        extractMods(file)
                    }

                modsTmp.deleteRecursively()
            }

            mixins.from("src/mixins/skyblock-api.client.mixins.json")
            mixins.from("src/mixins/skyblock-api.versioned.mixins.json")
            mixins.from("src/mixins/skyblock-api.versioned.${version.replace(".", "")}.mixins.json")

            runs {
                client {
                    args("--quickPlayMultiplayer=hypixel.net")

                    jvmArgs("-Ddevauth.enabled=true")
                    jvmArgs("-Dskyblockapi.debug=true")

                    beforeRun
                }
            }
        }
    }

    createVersion("1.21.5")
    createVersion("1.21.8", fabricApiVersion = provider { "0.129.0" }) {
        start = "1.21.6"
    }

    mappings {
        official()
    }
}

apiValidation {
    additionalSourceSets += "client"
    nonPublicMarkers += "org.jetbrains.annotations.ApiStatus\$Internal"

    ignoredProjects += "annotations"
    ignoredProjects += "compiler"
    ignoredPackages += "tech.thatgravyboat.skyblockapi.mixins"
    ignoredPackages += "tech.thatgravyboat.skyblockapi.impl"
}

repositories {
    maven(url = "https://repo.hypixel.net/repository/Hypixel/")
    maven(url = "https://maven.msrandom.net/repository/cloche")
    maven(url = "https://maven.msrandom.net/repository/root")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
    mavenLocal()
}

compactingResources {
    this.basePath = "repo"

    configureTask(tasks.getByName<ProcessResources>("process1218Resources"))
    configureTask(tasks.getByName<ProcessResources>("process1215Resources"))
    configureTask(tasks.getByName<ProcessResources>("processResources"))

    substituteFromDifferentFile("slayer", "slayers")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", libs.versions.minecraft.get())
    inputs.property("loader_version", libs.versions.fabric.loader.get())
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to libs.versions.minecraft.get(),
            "loader_version" to libs.versions.fabric.loader.get(),
            "kotlin_loader_version" to libs.versions.fabric.language.kotlin.get()
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release.set(21)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
}

tasks.withType<KspTask> {
    outputs.upToDateWhen { false }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.apiCheck { enabled = false }

artifacts {
    add("1215RuntimeElements", tasks["1215JarInJar"])
    add("1218RuntimeElements", tasks["1218JarInJar"])
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("SkyblockAPI")
                url.set("https://github.com/SkyblockAPI/SkyblockAPI")

                scm {
                    connection.set("git:https://github.com/SkyblockAPI/SkyblockAPI.git")
                    developerConnection.set("git:https://github.com/SkyblockAPI/SkyblockAPI.git")
                    url.set("https://github.com/SkyblockAPI/SkyblockAPI")
                }
            }
        }
    }
    repositories {
        maven {
            setUrl("https://maven.teamresourceful.com/repository/thatgravyboat/")
            credentials {
                username = System.getenv("MAVEN_USER") ?: providers.gradleProperty("maven_username").orNull
                password = System.getenv("MAVEN_PASS") ?: providers.gradleProperty("maven_password").orNull
            }
        }
    }
}

ksp {
    this@ksp.excludedSources.from(sourceSets.getByName("1215").kotlin.srcDirs)
    this@ksp.excludedSources.from(sourceSets.getByName("1218").kotlin.srcDirs)
    arg("meowdding.modules.project_name", "SkyblockAPI")
    arg("meowdding.modules.package", "tech.thatgravyboat.skyblockapi.generated")
    arg("meowdding.codecs.project_name", "SkyblockAPI")
    arg("meowdding.codecs.package", "tech.thatgravyboat.skyblockapi.generated")
}

// TODO temporary workaround for a cloche issue on certain systems, remove once fixed
val mcVersions = sourceSets.filterNot { it.name == SourceSet.MAIN_SOURCE_SET_NAME || it.name == SourceSet.TEST_SOURCE_SET_NAME }

tasks.withType<WriteClasspathFile>().configureEach {
    actions.clear()
    actions.add {
        generate()
        val file = output.get().toPath()
        file.writeText(file.readText().lines().joinToString(File.pathSeparator))
    }
}

tasks.register("release") {
    group = "skyblock-api"
    mcVersions.forEach {
        tasks.getByName("${it.name}JarInJar").let { task ->
            dependsOn(task)
            mustRunAfter(task)
        }
    }
}

tasks.register("cleanRelease") {
    group = "skyblock-api"
    listOf("clean", "release").forEach {
        tasks.getByName(it).let { task ->
            dependsOn(task)
            mustRunAfter(task)
        }
    }
}

tasks.withType<JarInJar>().configureEach {
    include { !it.name.endsWith("-dev.jar") }
}

afterEvaluate {
    tasks.named("createCommonApiStub", GenerateStubApi::class).configure {
        outputs.upToDateWhen { false }
        excludes.addAll(
            "org.jetbrains.kotlin",
            "me.owdding",
            "net.hypixel",
            "maven.modrinth",
            "com.fasterxml.jackson",
            "com.google",
            "com.ibm",
            "io.netty",
            "net.fabricmc:fabric-language-kotlin",
            "com.mojang",
            "net.fabricmc.fabric-api",
            "io.github.llamalad7:mixinextras",
            "net.minidev",
            "com.nimbusds",
            "tech.thatgravyboat",
            "net.msrandom"
        )
    }
}

tasks.register("setupForWorkflows") {
    mcVersions.flatMap {
        listOf("remap${it.name}CommonMinecraftNamed", "remap${it.name}ClientMinecraftNamed")
    }.mapNotNull { tasks.findByName(it) }.forEach {
        dependsOn(it)
        mustRunAfter(it)
    }
}
