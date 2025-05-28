import com.google.devtools.ksp.gradle.KspTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import tech.thatgravyboat.skyblockapi.item.deprecationMessage

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.ksp)
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

apiValidation {
    additionalSourceSets += "client"
    nonPublicMarkers += "org.jetbrains.annotations.ApiStatus\$Internal"

    ignoredPackages += "tech.thatgravyboat.skyblockapi.mixins"
    ignoredPackages += "tech.thatgravyboat.skyblockapi.impl"
}

repositories {
    maven(url = "https://repo.hypixel.net/repository/Hypixel/")
    maven(url = "https://api.modrinth.com/maven")
    maven(url = "https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1")
    maven(url = "https://maven.teamresourceful.com/repository/maven-public/")
}

dependencies {
    add("kotlinCompilerPluginClasspathClient", project(":compiler"))
    compileOnly(project(":annotations"))
    ksp(libs.bundles.meowdding)
    compileOnly(libs.bundles.meowdding)

    // To change the versions see the gradle.properties file
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())
    modImplementation(libs.bundles.fabric)

    modImplementation(libs.bundles.hypixel)
    include(libs.bundles.hypixel)

    include(libs.skyblockapi.repolib)
    implementation(libs.skyblockapi.repolib)

    modRuntimeOnly(libs.devauth)
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
            artifactId = "skyblock-api-${libs.versions.minecraft.get()}"
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

compactingResources {
    this.basePath = "repo"

    substituteFromDifferentFile("slayer", "slayers")
}

ksp {
    arg("meowdding.modules.project_name", project.name)
    arg("meowdding.modules.package", "tech.thatgravyboat.skyblockapi.generated")
    arg("meowdding.codecs.project_name", project.name)
    arg("meowdding.codecs.package", "tech.thatgravyboat.skyblockapi.generated")
}
