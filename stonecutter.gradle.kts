import org.gradle.kotlin.dsl.support.serviceOf
import org.jetbrains.kotlin.gradle.plugin.KotlinPlatformType

plugins {
    id("dev.kikugie.stonecutter")
    id("fabric-loom") version "1.11-SNAPSHOT" apply false
    id("org.jetbrains.kotlinx.binary-compatibility-validator") version "0.18.1"
    `maven-publish`
}

apiValidation {
    validationDisabled = false
    ignoredPackages.add("tech.thatgrabyboat.skyblockapi.impl")
}

stonecutter active "1.21.10"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"
    replacements.string {
        direction = eval(current.version, "> 1.21.5")
        replace("// moj_import <", "//!moj_import <")
    }

    filters.include("**/*.fsh", "**/*.vsh")
}


//<editor-fold desc="Publishing setup">
val componentFactory = project.serviceOf<SoftwareComponentFactory>()
val sbapiComponent = componentFactory.adhoc("sbapi")

stonecutter.versions.forEach { (project, version) ->
    val gradleFriendlyVersion = version.replace(".", "")
    val project = project(project)
    val apiElements = configurations.create(gradleFriendlyVersion + "apiElements") {
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
            attribute(Attribute.of("net.minecraft.version", String::class.java), version)
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            outgoing.artifact(tasks.named("remapJar"))
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
            attribute(TargetJvmVersion.TARGET_JVM_VERSION_ATTRIBUTE, 21)
            attribute(TargetJvmEnvironment.TARGET_JVM_ENVIRONMENT_ATTRIBUTE, objects.named(TargetJvmEnvironment.STANDARD_JVM))
            attribute(KotlinPlatformType.attribute, KotlinPlatformType.jvm)
            attribute(Category.CATEGORY_ATTRIBUTE, objects.named(Category.LIBRARY))
            attribute(LibraryElements.LIBRARY_ELEMENTS_ATTRIBUTE, objects.named(LibraryElements.JAR))
            attribute(Attribute.of("net.minecraft.version", String::class.java), version)
        }

        project.afterEvaluate {
            this@create.dependencies.addAll(configurations.named("runtimeOnly").get().dependencies)
            this@create.dependencies.addAll(configurations.named("modRuntimeOnly").get().dependencies)
            this@create.dependencies.addAll(configurations.named("api").get().dependencies)
            this@create.dependencies.addAll(configurations.named("modApi").get().dependencies)
            outgoing.artifact(tasks.named("remapJar"))
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
            attribute(Attribute.of("net.minecraft.version", String::class.java), version)
        }

        project.afterEvaluate {
            outgoing.artifact(tasks.named("remapSourcesJar"))
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
        }
    }
}
//</editor-fold>
