package tech.thatgravyboat.skyblockapi.utils.devutils

import me.owdding.ktmodules.Module
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugMultiSelect
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugSelect
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import java.nio.file.Path
import kotlin.io.path.Path

internal fun debugToggle(path: String, description: String = "", prefix: String = path): DebugToggle {
    return DebugToggle(SkyBlockAPI.id(path), description, SkyBlockApiDevUtils, prefix)
}

internal fun <T : Any> debugSelect(
    path: String,
    description: String = "",
    initialState: T?,
    states: List<T>,
    toString: (T) -> String = { it.toString() },
): DebugSelect<T> {
    return DebugSelect(SkyBlockAPI.id(path), description, SkyBlockApiDevUtils, initialState, toString, states)
}

internal inline fun <reified T : Enum<T>> debugSelect(
    path: String,
    description: String = "",
    initialState: T? = null,
): DebugSelect<T> = debugSelect(path, description, initialState, T::class.java.enumConstants.toList())


internal fun <T : Any> debugMultiSelect(
    path: String,
    description: String = "",
    initialState: Set<T> = emptySet(),
    toString: (T) -> String = { it.toString() },
    states: () -> List<T>,
): DebugMultiSelect<T> {
    return DebugMultiSelect(SkyBlockAPI.id(path), description, SkyBlockApiDevUtils, initialState, toString, states)
}

internal inline fun <reified T : Enum<T>> debugMultiSelect(
    path: String,
    description: String = "",
    initialState: Set<T> = emptySet(),
): DebugMultiSelect<T> = debugMultiSelect(path, description, initialState, { it.name }) { T::class.java.enumConstants.toList() }

@Module
internal data object SkyBlockApiDevUtils : DevUtils() {
    override val commandName: String = "sbapi toggle"
    override fun send(component: MutableComponent) = component.sendWithPrefix()

    override val supportsChangingDefault: Boolean = true
    override val path: Path = loadPath()
    val properties_: Map<String, String> = loadFromProperties(path)

    override val namespace: String = SkyBlockAPI.NAMESPACE

    // TODO: add debug int thing maybe?
    fun getInt(key: String, default: Int = 0): Int {
        return properties_[key].parseFormattedInt(default)
    }

    private fun loadPath(): Path {
        val specifiedPath = System.getenv("tech_thatgravyboat_sbapi_debug_property_path") ?: System.getProperty("sbapi.property_path")
        return if (specifiedPath != null) Path(specifiedPath)
        else McClient.config.resolve("sbapi.properties")
    }
}
