package tech.thatgravyboat.skyblockapi.utils.devutils.entries

import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.utils.devutils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import kotlin.reflect.KProperty

public class DebugToggle @JvmOverloads constructor(
    override val location: Identifier,
    override val description: String,
    override val devUtils: DevUtils,
    prefix: String = location.toString(),
) : DebugEntry {
    public val prefix: MutableComponent = Text.of(prefix, TextColor.DARK_PURPLE)

    private var state: Boolean = false

    init {
        devUtils.register(this)
    }

    public fun get(): Boolean = state

    public fun set(value: Boolean) {
        state = value
    }

    public fun toggle() {
        set(!get())
    }

    public fun reset() {
        set(false)
    }

    public fun getDefault(): Boolean = devUtils.getDefaultToggle(location)
    public fun toggleDefault(): Unit = devUtils.setToggleDefault(location, !getDefault())

    public operator fun getValue(any: Nothing?, property: KProperty<*>): Boolean = get()

    public operator fun getValue(any: Any?, property: KProperty<*>): Boolean = get()
}
