
import org.gradle.api.publish.internal.component.DefaultAdhocSoftwareComponent
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


evaluationDependsOnChildren()

//<editor-fold desc="Publishing setup">
val componentFactory = project.serviceOf<SoftwareComponentFactory>()
val sbapiComponent = componentFactory.adhoc("sbapi")

stonecutter.versions.forEach { (project, version) ->
    val gradleFriendlyVersion = version.replace(".", "")
    val project = project(project)

    val java = project.components.getByName<DefaultAdhocSoftwareComponent>("java")
    java.usages.forEach { context ->
        val config = configurations.create(gradleFriendlyVersion + "_" + context.name) {
            isCanBeResolved = false
            isCanBeConsumed = true

            attributes.addAllLater(context.attributes)
            outgoing.artifacts.addAll(context.artifacts)
            dependencies.addAll( context.dependencies)
            dependencyConstraints.addAll(context.dependencyConstraints)


            outgoing.capability("tech.thatgravyboat:skyblock-api-$version:${rootProject.version}")
            outgoing.capability("tech.thatgravyboat:skyblock-api:${rootProject.version}")
        }
        sbapiComponent.addVariantsFromConfiguration(config) {
            mapToOptional()
        }
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
