package tech.thatgravyboat.skyblockapi.api.profile.items.sacks

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import kotlin.time.Instant

internal data class SacksData(
    private val _counts: MutableMap<String, Int> = mutableMapOf(),
    private val _timestamps: MutableMap<String, Instant> = mutableMapOf(),
) {

    init {
        removeUnobtainable()
    }

    val counts: Map<String, Int> get() = _counts
    val timestamps: Map<String, Instant> get() = _timestamps

    constructor(entries: List<SackEntry>) : this() {
        entries.forEach { add(it) }
        removeUnobtainable()
    }

    private fun removeUnobtainable() {
        if (_counts.isEmpty() && _timestamps.isEmpty()) return // Minor optimization

        val unobtainableIds = SimpleItemAPI.unobtainableIds.mapNotNull { it.skyblockId }.toSet()
        _counts.keys.removeAll(unobtainableIds)
        _timestamps.keys.removeAll(unobtainableIds)
    }

    fun clear() {
        _counts.clear()
        _timestamps.clear()
    }

    fun add(entry: SackEntry) {
        _counts[entry.id] = entry.amount
        _timestamps[entry.id] = entry.lastUpdated
    }

    fun add(id: String, amount: Int) {
        _counts[id] = amount
        _timestamps[id] = currentInstant()
    }

    fun asSackEntries(): List<SackEntry> {
        return _counts
            .map { (id, amount) -> SackEntry(id, amount, _timestamps[id] ?: currentInstant()) }
            .sortedBy { it.lastUpdated.epochSeconds }
    }
}

@GenerateCodec
internal data class SackEntry(
    val id: String,
    val amount: Int,
    val lastUpdated: Instant = currentInstant(),
)
