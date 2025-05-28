package tech.thatgravyboat.skyblockapi.item

import org.gradle.api.Project
import org.gradle.api.provider.Provider
import org.jetbrains.kotlin.gradle.InternalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilation
import org.jetbrains.kotlin.gradle.plugin.KotlinCompilerPluginSupportPlugin
import org.jetbrains.kotlin.gradle.plugin.SubpluginArtifact
import org.jetbrains.kotlin.gradle.plugin.SubpluginOption

private val stuff = mutableMapOf<Project, String>()

var Project.deprecationMessage: String
    get() = stuff[this]!!
    set(value) {
        stuff[this] = value
    }

class RemoveNextVersionGradlePlugin : KotlinCompilerPluginSupportPlugin {
    @OptIn(InternalKotlinGradlePluginApi::class)
    override fun applyToCompilation(kotlinCompilation: KotlinCompilation<*>): Provider<List<SubpluginOption>> {
        println(kotlinCompilation)
        return kotlinCompilation.target.project.provider {
            buildList {
                add(SubpluginOption(key = "deprecatedMessage", value = stuff[kotlinCompilation.target.project]!!))
            }
        }
    }

    override fun getCompilerPluginId() = "tech.thatgravyboat.skyblockapi"

    override fun getPluginArtifact(): SubpluginArtifact = SubpluginArtifact("me.owdding.ktmodules", "KtModules", "1.0.3")

    override fun isApplicable(kotlinCompilation: KotlinCompilation<*>) = true
}
