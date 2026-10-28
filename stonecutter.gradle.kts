import org.gradle.api.publish.internal.component.DefaultAdhocSoftwareComponent
import org.gradle.kotlin.dsl.support.serviceOf

repositories {
    mavenCentral()
}

plugins {
    kotlin("jvm") apply false
    id("net.fabricmc.fabric-loom") version "1.18-SNAPSHOT" apply false
    `maven-publish`
}

stonecutter active "26.4"

stonecutter parameters {
    swaps["mod_version"] = "\"" + property("version") + "\";"
    swaps["minecraft"] = "\"" + node.metadata.version + "\";"

    replacements.string(current.parsed >= "26.4", "gui_extraction") {
        listOf("graphics", "guiGraphics").forEach { graphics ->
            listOf("extractRenderState", "extractBackground", "extractWidgetRenderState", "extractContents").forEach { method ->
                replace(
                    "override fun $method($graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float)",
                    "override fun $method($graphics: GuiGraphicsExtractor)",
                )
                replace("$method($graphics, mouseX, mouseY, a)", "$method($graphics)")
            }
            replace("this, $graphics, mouseX, mouseY", "this, $graphics, $graphics.mouseX(), $graphics.mouseY()")
            replace("{ $graphics, a ->", "{ $graphics ->")
            replace("extractDeferredElements(mouseX, mouseY, a)", "extractDeferredElements()")
            replace("GuiGraphicsExtractor $graphics, int mouseX, int mouseY, float a, CallbackInfo", "GuiGraphicsExtractor $graphics, CallbackInfo")
            replace("GuiGraphicsExtractor $graphics, int mouseX, int mouseY, CallbackInfo", "GuiGraphicsExtractor $graphics, CallbackInfo")
            replace("GuiGraphicsExtractor $graphics, Slot slot, int x, int y, CallbackInfo", "GuiGraphicsExtractor $graphics, Slot slot, CallbackInfo")
        }
    }
}

fun Project.isDummy(): Boolean = buildscript.sourceFile?.name == "dummy.gradle.kts"

val versionProjects = stonecutter.versions.map { project(it.project) }.filterNot { it.isDummy() }

stonecutter.versions.forEach {
    val project = project(it.project)
    if (!project.isDummy()) return@forEach
    project.tasks.configureEach {
        if (this.group == "stonecutter-impl") return@configureEach
        enabled = false
    }
}

evaluationDependsOnChildren()

tasks.register<Exec>("checkBreakingChangesInternal") {
    group = "skyblock-api"
    description = "Compares the changes"
    versionProjects.forEach { project ->
        dependsOn(project.tasks.named("updateKotlinAbi"), project.tasks.named("copyApiData"))
        mustRunAfter(project.tasks.named("updateKotlinAbi"))
    }

    this.workingDir = rootProject.layout.projectDirectory.asFile
    this.setCommandLine("kotlin", ".github/scripts/compare_dumps.main.kts", "local")

    isIgnoreExitValue = true

    doFirst {
        standardOutputCapture.start()
    }
    doLast {
        standardOutputCapture.stop()
    }
}
val stoneProject = project

versionProjects.forEach { project ->
    project.tasks.named("resetApiData") {
        mustRunAfter(stoneProject.tasks.named("checkBreakingChangesInternal"))
    }
}

tasks.register("checkBreakingChanges") {
    group = "skyblock-api"
    description = "Checks for breaking changes"

    versionProjects.forEach { project ->
        dependsOn(project.tasks.named("updateKotlinAbi"), project.tasks.named("copyApiData"), project.tasks.named("resetApiData"))
    }
    dependsOn("checkBreakingChangesInternal")
}

//<editor-fold desc="Publishing setup">
val componentFactory = project.serviceOf<SoftwareComponentFactory>()
val sbapiComponent = componentFactory.adhoc("sbapi")

stonecutter.versions.forEach { (project, version) ->
    val gradleFriendlyVersion = version.replace(".", "")
    val project = project(project)

    // we don't want to publish the dummy version
    if (project.isDummy()) return@forEach

    val java = project.components.getByName<DefaultAdhocSoftwareComponent>("java")
    java.usages.forEach { context ->
        val config = configurations.create(gradleFriendlyVersion + "_" + context.name) {
            isCanBeResolved = false
            isCanBeConsumed = true

            attributes.addAllLater(context.attributes)
            outgoing.artifacts.addAll(context.artifacts)
            dependencies.addAll(context.dependencies)
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
