plugins {
    `kotlin-dsl`
}

repositories {
    gradlePluginPortal()
    mavenCentral()
    maven("https://maven.teamresourceful.com/repository/maven-public/")
}

fun plugin(provider: Provider<PluginDependency>): Provider<String> = provider.map {
    "${it.pluginId}:${it.pluginId}.gradle.plugin:${it.version}"
}

dependencies {
    implementation(libs.kotlin.stdlib)
    implementation(libs.bundles.kotlin.gradle.plugin)
    implementation(libs.google.gson)
    implementation(plugin(libs.plugins.kotlin.symbol.processor))
    implementation(plugin(libs.plugins.meowdding.resources))
    implementation(plugin(libs.plugins.meowdding.auto.mixins))
    implementation(plugin(libs.plugins.kotlin.binary.compatibility.validator))
    implementation("dev.kikugie.stonecutter:dev.kikugie.stonecutter.gradle.plugin:0.8.3")
}

gradlePlugin {
    plugins {
        create("removeNextVersion") {
            id = "remove-next-version"
            implementationClass = "tech.thatgravyboat.skyblockapi.item.RemoveNextVersionGradlePlugin"
        }
    }
}
