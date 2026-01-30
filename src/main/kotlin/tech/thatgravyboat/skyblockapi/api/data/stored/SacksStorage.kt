package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.sacks.SackEntry
import tech.thatgravyboat.skyblockapi.api.profile.items.sacks.SacksData
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import kotlin.time.Instant

@Module
internal object SacksStorage {
    private val SACKS = StoredProfileData(
        2,
        ::SacksData,
        "sacks.json",
        true,
    ) { version ->
        when (version) {
            1 -> RecordCodecBuilder.create {
                it.group(
                    Codec.unboundedMap(Codec.STRING, Codec.INT).xmap(
                        { it.map { (key, value) -> SackEntry(key, value) } },
                        { mutableMapOf() },
                    ).optionalFieldOf("items", listOf()).forGetter(SacksData::asSackEntries),
                ).apply(it, ::SacksData)
            }
            2 -> RecordCodecBuilder.create { it.group(
                SkyblockAPICodecs.SackEntryCodec.codec().listOf().optionalFieldOf("items", listOf()).forGetter(SacksData::asSackEntries),
            ).apply(it, ::SacksData) }
            else -> CodecUtils.unit { SacksData() }
        }
    }

    val counts: Map<String, Int> get() = SACKS.get()?.counts ?: emptyMap()
    val timestamps: Map<String, Instant> get() = SACKS.get()?.timestamps ?: emptyMap()

    // Returns the old value
    fun updateItem(item: String, amount: Int): Int {
        val oldValue = this.counts[item]
        if (oldValue == amount) return oldValue
        SACKS.get()?.add(item, amount)
        SACKS.save()
        return oldValue ?: 0
    }

    fun updateItemValue(item: String, diff: Int) {
        val prevAmount = this.counts[item] ?: 0
        val newAmount = (prevAmount + diff).coerceAtLeast(0)
        updateItem(item, newAmount)
    }

    fun clear() {
        SACKS.get()?.clear()
        SACKS.save()
    }

}

