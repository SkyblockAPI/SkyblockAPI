package tech.thatgravyboat.skyblockapi.utils.devutils.entries

import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.utils.devutils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.replaceWith
import kotlin.reflect.KProperty

public class DebugMultiSelect<T : Any>(
    override val location: Identifier,
    override val description: String,
    override val devUtils: DevUtils,
    private val initialState: Set<T> = emptySet(),
    private val toString: (T) -> String,
    private val statesProvider: () -> List<T>,
) : DebugEntry {
    private val defaultState: Set<T> = initialState.toSet()

    private var isInitialized = false
    private val state: MutableSet<T> = mutableSetOf()

    private fun ensureInitialized() {
        if (isInitialized) return
        isInitialized = true
        state.addAll(initialState)

        if (!canSetDefault()) return
        val stored = devUtils.values[location] ?: return
        val items = deserialize(stored)

        state.clear()
        state.addAll(items)
    }

    init {
        devUtils.register(this)
    }

    public fun get(): Set<T> {
        ensureInitialized()
        return state
    }

    public fun set(values: Collection<T>) {
        ensureInitialized()
        state.replaceWith(values.intersect(statesProvider().toSet()))
    }

    public fun isSelected(value: T): Boolean {
        ensureInitialized()
        return value in state
    }

    public fun isSelectedByName(name: String): Boolean = getByName(name)?.let(::isSelected) == true

    public fun toggle(value: T) {
        ensureInitialized()
        if (!state.add(value)) state.remove(value)
    }

    public fun toggleByName(name: String) {
        getByName(name)?.let { toggle(it) }
    }

    public fun getByName(name: String?): T? = statesProvider().find { toString(it) == name }

    public fun states(): List<String> = statesProvider().map(toString)

    public fun setBySerialized(raw: String?) {
        ensureInitialized()
        if (raw == null) {
            reset()
            return
        }
        val items = raw.split(",")
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNull { getByName(it) }
        set(items)
    }

    public fun serialized(): String {
        ensureInitialized()
        return serialize(state)
    }

    public fun reset() {
        set(defaultState)
    }

    public fun getDefaultState(): Set<T> = defaultState
    public fun isDefaultState(): Boolean = get() == defaultState

    public fun getDefaultStateSerialized(): String = defaultState.map(toString).sorted().joinToString(",")

    public fun getIsDefaultSelectedByName(name: String): Boolean {
        if (!canSetDefault()) return false
        return deserialize(getDefault()).any { toString(it) == name }
    }

    public fun setDefaultSelectedByName(name: String, selected: Boolean) {
        if (!canSetDefault()) return
        val default = deserialize(getDefault())
        val value = getByName(name) ?: return

        if (selected) {
            if (!default.add(value)) return
        } else {
            if (!default.remove(value)) return
        }

        setDefault(serialize(default))
    }

    public fun getDefault(): String {
        if (!canSetDefault()) return getDefaultStateSerialized()
        return devUtils.getDefaultMultiSelect(location) ?: getDefaultStateSerialized()
    }

    public fun setDefault(serialized: String?) {
        if (!canSetDefault()) return
        devUtils.setMultiSelectDefault(location, serialized ?: getDefaultStateSerialized())
    }

    public fun hasDefault(): Boolean = getDefault() != getDefaultStateSerialized()

    private fun deserialize(string: String): MutableSet<T> {
        return string.split(',')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .mapNotNullTo(mutableSetOf(), ::getByName)
    }

    private fun serialize(set: Set<T>): String {
        return set.map(toString).sorted().joinToString(",")
    }

    public operator fun getValue(any: Nothing?, property: KProperty<*>): Set<T> = get()
    public operator fun getValue(any: Any?, property: KProperty<*>): Set<T> = get()
}
