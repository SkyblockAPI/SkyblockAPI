
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

stonecutter active "26.1"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"

    filters.include("**/*.fsh", "**/*.vsh")

    replacements.string("graphics") {
        direction = eval(current.version, "< 26.1")
        replace(
            "GuiGraphicsExtractor",
            "GuiGraphics"
        )
    }

    replacements.regex {
        direction = eval(current.version, "< 1.21.11")
        replace(
            "import net.minecraft.resources.Identifier(?!;)", "import net.minecraft.resources.ResourceLocation as Identifier",
            "import net.minecraft.resources.ResourceLocation as Identifier", "import net.minecraft.resources.Identifier"
        )
    }

    replacements.regex {
        direction = eval(current.version, "< 1.21.11")
        replace(
            "import net.minecraft.util.IdentifierPattern(?!;)", "import net.minecraft.util.ResourceLocationPattern as IdentifierPattern",
            "import net.minecraft.util.ResourceLocationPattern as IdentifierPattern", "import net.minecraft.util.IdentifierPattern"
        )
    }

    replacements.string {
        direction = eval(current.version, "< 1.21.11")
        replace("import net.minecraft.advancements.criterion", "import net.minecraft.advancements.critereon")
    }

    replacements.string {
        direction = eval(current.version, "< 26.1")
        replace(
            "import net.fabricmc.fabric.api.client.command.v2.ClientCommands",
            "import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager as ClientCommands",
        )
        replace(
            "import net.minecraft.client.multiplayer.chat.GuiMessage",
            "import net.minecraft.client.GuiMessage",
        )
        replace(
            "import net.minecraft.client.multiplayer.chat.GuiMessageTag",
            "import net.minecraft.client.GuiMessageTag",
        )
        replace(
            "import net.minecraft.client.renderer.state.gui.BlitRenderState",
            "import net.minecraft.client.gui.render.state.BlitRenderState"
        )
        replace(
            "import net.minecraft.client.renderer.state.gui.GuiElementRenderState",
            "import net.minecraft.client.gui.render.state.GuiElementRenderState"
        )
        replace("Lnet/minecraft/client/multiplayer/chat/GuiMessage", "Lnet/minecraft/client/GuiMessage")
        replace("Lnet/minecraft/client/multiplayer/chat/GuiMessageTag", "Lnet/minecraft/client/GuiMessageTag")
    }

    replacements.regex {
        direction = eval(current.version, "< 26.1")
        replace(
            "import net.minecraft.client.gui.GuiGraphicsExtractor(?!;)", "import net.minecraft.client.gui.GuiGraphics as GuiGraphicsExtractor",
            "import net.minecraft.client.gui.GuiGraphics as GuiGraphicsExtractor", "import net.minecraft.client.gui.GuiGraphicsExtractor"
        )
    }
}


//<editor-fold desc="Publishing setup">
val componentFactory = project.serviceOf<SoftwareComponentFactory>()
val sbapiComponent = componentFactory.adhoc("sbapi")
val minecraftVersionAttribute = Attribute.of("net.minecraft.version", String::class.java)
val remappedAttribute = Attribute.of("net.fabricmc.remapped", String::class.java)

stonecutter.versions.forEach { (project, version) ->
    fun isObfuscated() = stonecutter.eval(version, "<=1.21.11")

    fun runIfObfuscated(action: () -> Unit) {
        if (isObfuscated()) action()
    }

    fun <T> selectIfObfuscated(obfuscated: T, unobfuscated: T) = if (isObfuscated()) obfuscated else unobfuscated

    val gradleFriendlyVersion = version.replace(".", "")
    val project = project(project)

    println("Creating publication for $version")
    runIfObfuscated {
        val remappedApiElements = configurations.create(gradleFriendlyVersion + "remappedApiElements") {
            isCanBeResolved = false
            isCanBeConsumed = true

            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
                attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
                attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
                attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
                attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
                attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
                attribute(minecraftVersionAttribute, version)
                attribute(remappedAttribute, "true")
            }

            project.afterEvaluate {
                this@create.dependencies.addAll(configurations.named("api").get().dependencies)
                this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
                println("Adding remapped api for $version")
                outgoing.artifact(tasks.named("remapJar")) {
                    classifier += "-api"
                }
            }

            outgoing.capability("tech.thatgravyboat:skyblock-api-$version-remapped:${rootProject.version}")
            outgoing.capability("tech.thatgravyboat:skyblock-api:${rootProject.version}")
        }
        val remappedRuntimeElements = configurations.create(gradleFriendlyVersion + "remappedRuntimeElements") {
            isCanBeResolved = false
            isCanBeConsumed = true

            attributes {
                attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
                attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
                attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
                attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
                attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
                attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
                attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
                attribute(minecraftVersionAttribute, version)
                attribute(remappedAttribute, "true")
            }

            project.afterEvaluate {
                this@create.dependencies.addAll(configurations.named("runtimeOnly").get().dependencies)
                this@create.dependencies.addAll(configurations.named("modRuntimeOnly").get().dependencies)
                this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)

                this@create.dependencies.addAll(configurations.named("api").get().dependencies)
                outgoing.artifact(tasks.named("remapJar")) {
                    classifier += "-runtime"
                }
                println("Adding remapped runtime for $version")
            }

            outgoing.capability("tech.thatgravyboat:skyblock-api-$version-remapped:${rootProject.version}")
            outgoing.capability("tech.thatgravyboat:skyblock-api:${rootProject.version}")
        }

        sbapiComponent.addVariantsFromConfiguration(remappedApiElements) {
            mapToOptional()
        }
        sbapiComponent.addVariantsFromConfiguration(remappedRuntimeElements) {
            mapToOptional()
        }
    }

    val apiElements = configurations.create(gradleFriendlyVersion + "apiElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, selectIfObfuscated(21, 25))
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            runIfObfuscated {
                this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            }
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
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, selectIfObfuscated(21, 25))
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
            runIfObfuscated {
                this@create.dependencies.addAll(configurations.named("modRuntimeOnly").get().dependencies)
                this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            }
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
