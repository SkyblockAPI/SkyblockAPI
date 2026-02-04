private val stonecutter = project.extensions.getByName("stonecutter") as dev.kikugie.stonecutter.build.StonecutterBuildExtension


repositories {
    fun scopedMaven(url: String, vararg paths: String) = maven(url) { content { paths.forEach(::includeGroupAndSubgroups) } }

    scopedMaven("https://pkgs.dev.azure.com/djtheredstoner/DevAuth/_packaging/public/maven/v1", "me.djtheredstoner")
    scopedMaven("https://repo.hypixel.net/repository/Hypixel", "net.hypixel")
    scopedMaven("https://maven.parchmentmc.org/", "org.parchmentmc")
    scopedMaven("https://api.modrinth.com/maven", "maven.modrinth")
    scopedMaven(
        "https://maven.teamresourceful.com/repository/maven-public/",
        "earth.terrarium",
        "com.teamresourceful",
        "tech.thatgravyboat",
        "me.owdding",
        "com.terraformersmc"
    )
    scopedMaven("https://maven.nucleoid.xyz/", "eu.pb4")
    mavenCentral()
}
afterEvaluate {
    fun isNewVersioning() = stonecutter.eval(stonecutter.current.version, ">=26.1")
    dependencies {
        val api = if (isNewVersioning()) "api" else "modApi"
        val implementation = if (isNewVersioning()) "implementation" else "modImplementation"
        val runtimeOnly = if (isNewVersioning()) "runtimeOnly" else "modRuntimeOnly"

        "minecraft"(versionedCatalog["minecraft"])

        "compileOnly"(project(":annotations"))
        "compileOnly"(versionedCatalog.bundles["meowdding"])
        "ksp"(versionedCatalog.bundles["meowdding"])

        implementation(versionedCatalog["fabric.language.kotlin"])

        "api"(versionedCatalog["meowdding.item.dfu"]) {
            capabilities { requireCapability("me.owdding:item-data-fixer-${stonecutter.current.version}") }
        }
        "include"(versionedCatalog["meowdding.item.dfu"]) {
            capabilities { requireCapability("me.owdding:item-data-fixer-${stonecutter.current.version}-remapped") }
        }

        "api"(versionedCatalog["hypixel.modapi"])
        implementation(versionedCatalog.bundles["hypixel"])
        "include"(versionedCatalog["hypixel.modapi.fabric"])

        api(versionedCatalog["skyblockapi.repolib"])
        "include"(versionedCatalog["skyblockapi.repolib"])

        runtimeOnly(versionedCatalog["devauth"])

        implementation(versionedCatalog["fabric.api"])
        implementation(versionedCatalog["fabric.loader"])
    }
}
