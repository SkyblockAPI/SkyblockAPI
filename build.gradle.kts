import com.google.devtools.ksp.gradle.KspTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinVersion
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tech.thatgravyboat.skyblockapi.item.deprecationMessage

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

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions {
        languageVersion = KotlinVersion.KOTLIN_2_0
        freeCompilerArgs.addAll(
            "-Xmulti-platform",
            "-Xno-check-actual",
            "-Xexpect-actual-classes",
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
        dependencies {
            compileOnly(project(":annotations"))
            modCompileOnly.bundle(libs.bundles.meowdding)
            modCompileOnlyApi.bundle(libs.bundles.meowdding)

            modImplementation(libs.fabric.language.kotlin)
            modImplementation.bundle(libs.bundles.hypixel)
            modImplementation(libs.skyblockapi.repolib)

            modRuntimeOnly(libs.devauth)
        }
    }

    fun createVersion(
        name: String,
        version: String = name,
        loaderVersion: Provider<String> = libs.versions.fabric.loader,
        fabricApiVersion: Provider<String> = libs.versions.fabric.api,
    ) {
        fabric(name) {
            includedClient()
            minecraftVersion = version
            this.loaderVersion = loaderVersion.get()

            include(libs.skyblockapi.repolib)
            include(libs.hypixel.modapi.fabric)

            metadata {
                entrypoint("client", "tech.thatgravyboat.skyblockapi.api.SkyBlockAPI::postInit")
                entrypoint("main", "tech.thatgravyboat.skyblockapi.utils.regex.Regexes::load")
                entrypoint("main", "tech.thatgravyboat.skyblockapi.api.SkyBlockAPI::init")

                dependency {
                    modId = "fabric-language-kotlin"
                    required = true
                    version("*")
                }

                dependency {
                    modId = "fabric"
                    required = true
                    version("*")
                }
            }

            dependencies {
                fabricApi(fabricApiVersion.get(), name)
            }

            mixins.from("src/common/main/mixins/skyblock-api.client.mixins.json")
            mixins.from("src/common/main/mixins/skyblock-api.versioned.mixins.json")

            runs {
                client()
            }
        }
    }

    createVersion("1.21.5")
    createVersion("1.21.6")


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
    options.release.set(targetJavaVersion)
}

tasks.withType<KotlinCompile>().configureEach {
    compilerOptions.jvmTarget.set(JvmTarget.fromTarget(targetJavaVersion.toString()))
}

tasks.withType<KspTask> {
    outputs.upToDateWhen { false }
}

tasks.withType<Jar> {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.apiCheck { enabled = false }

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "skyblock-api"

            artifact(tasks["1215JarInJar"])
            artifact(tasks["1216JarInJar"])
            artifact(tasks["generateMetadataFileForMavenPublication"].outputs.files.first()) {
                extension = "module"
            }

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

compactingResources {
    this.basePath = "repo"

    substituteFromDifferentFile("slayer", "slayers")
}

ksp {
    this@ksp.excludedSources.from(sourceSets.getByName("1215").kotlin.srcDirs)
    this@ksp.excludedSources.from(sourceSets.getByName("1216").kotlin.srcDirs)
    arg("meowdding.modules.project_name", project.name)
    arg("meowdding.modules.package", "tech.thatgravyboat.skyblockapi.generated")
    arg("meowdding.codecs.project_name", project.name)
    arg("meowdding.codecs.package", "tech.thatgravyboat.skyblockapi.generated")
}
