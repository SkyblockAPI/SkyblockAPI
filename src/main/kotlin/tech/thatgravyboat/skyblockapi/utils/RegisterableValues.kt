package tech.thatgravyboat.skyblockapi.utils

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.PostInitEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.utils.devutils.SkyBlockApiDevUtils.debugString
import tech.thatgravyboat.skyblockapi.utils.devutils.entries.DebugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.pluralize
import tech.thatgravyboat.skyblockapi.utils.text.Text

/** Needs to be loaded by [me.owdding.ktmodules.Module], or by something else before post init */
public abstract class RegisterableValues<T : Any> internal constructor() {
    protected abstract val name: String
    /** Used for the debug command */
    protected open val pluralName: String? = null

    protected abstract val debugToggle: DebugToggle

    public val registered: Map<String, T>
        field = mutableMapOf()
    public val values: Collection<T>
        get() = registered.values

    internal val missing: Set<String>
        field = mutableSetOf<String>()

    protected fun checkMissingKey(key: String) {
        if (!finishedRegistering) return
        missing.add(key)
        debugString(debugToggle) { "Unknown $name value with key $key" }
    }

    protected fun getOrCreate(key: String, factory: (key: String) -> T): T {
        return registered.getOrPut(key) {
            checkMissingKey(key)
            factory(key)
        }
    }

    protected open fun debugCommand() {
        McClient.clipboard = missing.joinToString(prefix = "[", postfix = "]")
        val size = missing.size
        Text.sendDebug("Copied $size unknown ${pluralize(size, name, pluralName)} to clipboard.")
    }

    @Subscription(inherited = true)
    internal fun registerCommands(event: RegisterSkyblockApiCommandsEvent) {
        event.registerWithCallback("unknown $name") {
            if (missing.isEmpty()) {
                Text.sendDebug("No unknown $name")
                return@registerWithCallback
            }
            debugCommand()
        }
    }

    @Module
    internal companion object {
        private var finishedRegistering = false

        @Subscription(PostInitEvent::class)
        private fun onPostInit() {
            finishedRegistering = true
        }
    }
}
