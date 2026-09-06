
import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") apply false
    id("dev.kikugie.stonecutter")
    id("net.fabricmc.fabric-loom-remap") version "1.15-SNAPSHOT" apply false
    id("net.fabricmc.fabric-loom") version "1.15-SNAPSHOT" apply false
    `maven-publish`
}

stonecutter active "26.3"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"

    filters.include("**/*.fsh", "**/*.vsh")

    replacements.regex {
        direction = eval(current.version, "< 26.2")
        replace(
            "import net.minecraft.advancements.predicates.BlockPredicate", "import net.minecraft.advancements.criterion.BlockPredicate",
            "import net.minecraft.advancements.criterion.BlockPredicate", "import net.minecraft.advancements.predicates.BlockPredicate"
        )
    }
}


//<editor-fold desc="Publishing setup">
val componentFactory = project.serviceOf<SoftwareComponentFactory>()
val sbapiComponent = componentFactory.adhoc("sbapi")
val minecraftVersionAttribute = Attribute.of("net.minecraft.version", String::class.java)
val remappedAttribute = Attribute.of("net.fabricmc.remapped", String::class.java)

stonecutter.versions.forEach { (project, version) ->
    val gradleFriendlyVersion = version.replace(".", "")
    val project = project(project)

    println("Creating publication for $version")

    val apiElements = configurations.create(gradleFriendlyVersion + "apiElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            outgoing.artifact(tasks.named("jar")) {
                classifier += "-api"
            }
            println("Adding api for $version")
        }

        outgoing.capability("tech.thatgravyboat:skyblock-api-$version:${rootProject.version}")
        outgoing.capability("tech.thatgravyboat:skyblock-api:${rootProject.version}")
    }
    val runtimeElements = configurations.create(gradleFriendlyVersion + "runtimeElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 25)
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            this@create.dependencies.addAll(configurations.named("runtimeOnly").get().dependencies)
            outgoing.artifact(tasks.named("jar")) {
                classifier += "-runtime"
            }
            println("Adding runtime for $version")
        }

        outgoing.capability("tech.thatgravyboat:skyblock-api-$version:${rootProject.version}")
        outgoing.capability("tech.thatgravyboat:skyblock-api:${rootProject.version}")
    }

    val sourcesElements = configurations.create(gradleFriendlyVersion + "sources") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.DOCUMENTATION))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(DocsType.DOCS_TYPE_ATTRIBUTE, objects.named(DocsType.SOURCES))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            outgoing.artifact(tasks.named("sourcesJar"))
            println("Adding sources for $version")
        }

        outgoing.capability("tech.thatgravyboat:skyblock-api-$version:${rootProject.version}")
        outgoing.capability("tech.thatgravyboat:skyblock-api:${rootProject.version}")
    }

    sbapiComponent.addVariantsFromConfiguration(apiElements) {
        mapToOptional()
    }
    sbapiComponent.addVariantsFromConfiguration(runtimeElements) {
        mapToOptional()
    }
    sbapiComponent.addVariantsFromConfiguration(sourcesElements) {
        mapToOptional()
    }
}

publishing {
    publications {
        create("skyblock-api", MavenPublication::class.java) {
            from(sbapiComponent)
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
//</editor-fold>
