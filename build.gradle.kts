import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "2.0.20"
    id("fabric-loom") version "1.8-SNAPSHOT"
    id("maven-publish")
    id("com.google.devtools.ksp") version "2.0.20-1.0.25"
}

version = project.property("mod_version") as String
group = project.property("maven_group") as String

base {
    archivesName.set(project.property("archives_base_name") as String)
}

val targetJavaVersion = 21
java {
    toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    withSourcesJar()
}

loom {
    splitEnvironmentSourceSets()

    runs {
        getByName("client") {
            programArg("--quickPlayMultiplayer=hypixel.net")
            vmArg("-Ddevauth.enabled=true")
            vmArg("-Dskyblockapi.debug=true")
        }
    }

    mods {
        register("skyblock-api") {
            sourceSet("main")
            sourceSet("client")
        }
    }
}

repositories {
    maven(url = "https://repo.hypixel.net/repository/Hypixel/")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
}

dependencies {
    compileOnly(ksp(project(":annotations"))!!)

    // To change the versions see the gradle.properties file
    minecraft("com.mojang:minecraft:${project.property("minecraft_version")}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${project.property("loader_version")}")
    modImplementation("net.fabricmc:fabric-language-kotlin:${project.property("kotlin_loader_version")}")

    // Fabric API. This is technically optional, but you probably want it anyway.
    modImplementation("net.fabricmc.fabric-api:fabric-api:${project.property("fabric_version")}")

    modImplementation("net.hypixel:mod-api:1.0.1")
    modImplementation("maven.modrinth:hypixel-mod-api:1.0.1+build.1+mc1.21")

    modRuntimeOnly("me.djtheredstoner:DevAuth-fabric:1.2.1")
}

tasks.processResources {
    inputs.property("version", project.version)
    inputs.property("minecraft_version", project.property("minecraft_version"))
    inputs.property("loader_version", project.property("loader_version"))
    filteringCharset = "UTF-8"

    filesMatching("fabric.mod.json") {
        expand(
            "version" to project.version,
            "minecraft_version" to project.property("minecraft_version"),
            "loader_version" to project.property("loader_version"),
            "kotlin_loader_version" to project.property("kotlin_loader_version")
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

publishing {
    publications {
        create<MavenPublication>("maven") {
            artifactId = "skyblock-api-${project.property("minecraft_version")}"
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
