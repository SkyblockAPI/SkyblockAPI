import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    kotlin("jvm")
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.15.2" apply false
    id("net.fabricmc.fabric-loom") version "1.15.2" apply false
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.1"
    `maven-publish`
}

apiValidation {
    validationDisabled = false
    ignoredPackages.add("tech.thatgrabyboat.skyblockapi.impl")
}

stonecutter active "1.21.11"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    replacements.string {
        direction = eval(current.version, "> 1.21.5")
        replace("// moj_import <", "//!moj_import <")
    }

    filters.include("**/*.fsh", "**/*.vsh")


    replacements.regex {
        direction = eval(current.version, "< 1.21.11")
        replace("import net.minecraft.resources.Identifier(?!;)", "import net.minecraft.resources.ResourceLocation as Identifier")
        reverse("import net.minecraft.resources.ResourceLocation as Identifier", "import net.minecraft.resources.Identifier")
    }

    replacements.regex {
        direction = eval(current.version, "< 1.21.11")
        replace("import net.minecraft.util.IdentifierPattern(?!;)", "import net.minecraft.util.ResourceLocationPattern as IdentifierPattern")
        reverse("import net.minecraft.util.ResourceLocationPattern as IdentifierPattern", "import net.minecraft.util.IdentifierPattern")
    }

    replacements.regex {
        direction = eval(current.version, "< 1.21.11")
        replace("import net.minecraft.advancements.criterion", "import net.minecraft.advancements.critereon")
        reverse("import net.minecraft.advancements.critereon", "import net.minecraft.advancements.criterion")
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
    val remappedApiElements = configurations.create(gradleFriendlyVersion + "remappedApiElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)// TODO
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "true")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            runCatching {
                this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
                outgoing.artifact(tasks.named("remapJar"))
            }
        }

        outgoing.capability("tech.thatgravyboat:skyblock-api-$version-remapped:${rootProject.version}")
        outgoing.capability("tech.thatgravyboat:skyblock-api:${rootProject.version}")
    }
    val apiElements = configurations.create(gradleFriendlyVersion + "apiElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_API))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)// TODO
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "false")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            runCatching {
                this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            }
            outgoing.artifact(tasks.named("jar"))
        }

        outgoing.capability("tech.thatgravyboat:skyblock-api-$version:${rootProject.version}")
        outgoing.capability("tech.thatgravyboat:skyblock-api:${rootProject.version}")
    }

    val remappedRuntimeElements = configurations.create(gradleFriendlyVersion + "remappedRuntimeElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)// TODO
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(minecraftVersionAttribute, version)
            attribute(remappedAttribute, "true")
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("runtimeOnly").get().dependencies)
            runCatching {
                this@create.dependencies.addAll(configurations.named("modRuntimeOnly").get().dependencies)
                this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            }
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            runCatching {
                outgoing.artifact(tasks.named("remapJar"))
            }
        }

        outgoing.capability("tech.thatgravyboat:skyblock-api-$version-remapped:${rootProject.version}")
        outgoing.capability("tech.thatgravyboat:skyblock-api:${rootProject.version}")
    }
    val runtimeElements = configurations.create(gradleFriendlyVersion + "runtimeElements") {
        isCanBeResolved = false
        isCanBeConsumed = true

        attributes {
            attribute(Usage.USAGE_ATTRIBUTE, objects.named(Usage.JAVA_RUNTIME))
            attribute(Bundling.BUNDLING_ATTRIBUTE, objects.named(Bundling.EXTERNAL))
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21) // TODO
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
            runCatching {
                this@create.dependencies.addAll(configurations.named("modRuntimeOnly").get().dependencies)
                this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            }
            outgoing.artifact(tasks.named("jar"))
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
        }

        outgoing.capability("tech.thatgravyboat:skyblock-api-$version:${rootProject.version}")
        outgoing.capability("tech.thatgravyboat:skyblock-api:${rootProject.version}")
    }

    sbapiComponent.addVariantsFromConfiguration(remappedApiElements) {
        mapToOptional()
    }
    sbapiComponent.addVariantsFromConfiguration(remappedRuntimeElements) {
        mapToOptional()
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
