package tech.thatgravyboat.skyblockapi.utils.devutils.entries

import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.utils.devutils.DevUtils
import kotlin.reflect.KProperty

public class DebugSelect<T : Any>(
    override val location: Identifier,
    override val description: String,
    override val devUtils: DevUtils,
    initialState: T? = null,
    private val toString: (T) -> String,
    private val states: List<T>,
) : DebugEntry {
    private val defaultState: T? = initialState
    private var state: T? = initialState

    init {
        devUtils.register(this)
    }

    public fun get(): T? = state
    public fun set(value: T?) {
        this.state = fixValue(value)
    }

    public fun getByName(name: String?): T? = states.find { toString(it) == name }
    public fun setByName(name: String?): Unit = set(getByName(name))

    public fun states(): List<String> = states.map(toString)

    public fun stateName(): String? = get()?.let(toString)

    public fun reset() {
        set(defaultState)
    }

    public fun getDefaultState(): T? = defaultState
    public fun isDefaultState(): Boolean = get() == this.defaultState
    public fun getDefaultStateName(): String? = this.defaultState?.let(toString)
    public fun isNullable(): Boolean = this.defaultState == null


    public fun getDefault(): String? {
        if (!canSetDefault()) return getDefaultStateName()
        return fixNameValue(devUtils.getDefaultSelect(location))
    }

    public fun setDefault(value: String?) {
        if (!canSetDefault()) return
        // This is to make sure you can't set it to null if it's not nullable
        devUtils.setSelectDefault(location, fixNameValue(value))
    }

    public fun hasDefault(): Boolean = getDefault() != getDefaultStateName()

    public operator fun getValue(any: Nothing?, property: KProperty<*>): T? = get()
    public operator fun getValue(any: Any?, property: KProperty<*>): T? = get()

    private fun fixNameValue(value: String?): String? = value ?: getDefaultStateName()

    // If the default value isn't null, we don't let the user use null
    private fun fixValue(value: T?): T? = value ?: defaultState
}
