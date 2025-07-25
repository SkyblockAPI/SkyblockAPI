package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.sacks.SackEntry
import tech.thatgravyboat.skyblockapi.api.profile.items.sacks.SacksData
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import java.util.*

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
                        { it.map { (key, value) -> SackEntry(key, value) }.toMutableList() },
                        { mutableMapOf() },
                    ).optionalFieldOf("items").forGetter { getter -> Optional.of(getter.items) },
                ).apply(it, SkyblockAPICodecs::createSacksDataCodec)
            }

            2 -> SacksData.CODEC
            else -> Codec.unit { SacksData() }
        }
    }

    val items: MutableList<SackEntry>
        get() = SACKS.get()?.items ?: mutableListOf()

    fun updateItem(item: String, amount: Int) {
        val entry = items.find { it.id == item }
        if (entry?.amount == amount) return
        entry?.let { items.remove(it) }
        items.add(SackEntry(item, amount))
        SACKS.save()
    }

    fun updateItemValue(item: String, diff: Int) {
        val prevAmount = items.find { it.id == item }?.amount ?: 0
        val newAmount = (prevAmount + diff).coerceAtLeast(0)
        updateItem(item, newAmount)
    }

}

