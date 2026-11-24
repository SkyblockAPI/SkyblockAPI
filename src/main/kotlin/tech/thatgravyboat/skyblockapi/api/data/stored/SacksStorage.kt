package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.sacks.SackEntry
import tech.thatgravyboat.skyblockapi.api.profile.items.sacks.SacksData
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs.getCodec
import tech.thatgravyboat.skyblockapi.utils.codecs.CodecUtils
import tech.thatgravyboat.skyblockapi.utils.extentions.forNullGetter
import tech.thatgravyboat.skyblockapi.utils.extentions.replaceWith
import java.util.Optional
import kotlin.String
import kotlin.time.Instant

@Module
internal object SacksStorage {
    private val IGNORED_ITEMS = listOf("common_hook", "nether_star").map(SkyBlockId::item) // Items that Hypixel shows in the Sack but don't go in there.

    private val oldCodec = (RecordCodecBuilder.mapCodec<SackEntry> {
        it.group(
            getCodec<String>().fieldOf("id").forGetter { getter -> getter.id.skyblockId },
            getCodec<Int>().fieldOf("amount").forGetter { getter -> getter.amount },
            getCodec<Instant>().optionalFieldOf("lastUpdated").forGetter { getter -> Optional.of(getter.lastUpdated) },
        ).apply(it) { p_id, p_amount, p_lastUpdated ->
            when {
                p_lastUpdated.isPresent -> SackEntry(id = SkyBlockId.item(p_id), amount = p_amount, lastUpdated = p_lastUpdated.get())
                else -> SackEntry(id = SkyBlockId.item(p_id), amount = p_amount)
            }
        }
    }).codec()

    private val SACKS = StoredProfileData(
        4,
        ::SacksData,
        "sacks.json",
    ) { version ->
        when (version) {
            1 -> RecordCodecBuilder.create {
                it.group(
                    Codec.unboundedMap(Codec.STRING, Codec.INT).xmap(
                        { it.map { (key, value) -> SackEntry(SkyBlockId.item(key), value) } },
                        { mutableMapOf() },
                    ).optionalFieldOf("items", listOf()).forGetter(SacksData::asSackEntries),
                ).apply(it, ::SacksData)
            }
            2, 3 -> RecordCodecBuilder.create {
                it.group(
                    oldCodec.listOf().xmap(
                        { list -> list.filterNot { entry -> entry.id.isIgnoredItem() } },
                        { list -> list.filterNot { entry -> entry.id.isIgnoredItem() } },
                    ).optionalFieldOf("items", listOf()).forGetter(SacksData::asSackEntries),
                    CodecUtils.list(ItemStack.OPTIONAL_CODEC).optionalFieldOf("ownedSacks").forNullGetter(SacksData::ownedSacks)
                ).apply(it, ::SacksData)
            }
            4 -> RecordCodecBuilder.create {
                it.group(
                    SkyblockAPICodecs.SackEntryCodec.codec().listOf().xmap(
                        { list -> list.filterNot { entry -> entry.id.isIgnoredItem() } },
                        { list -> list.filterNot { entry -> entry.id.isIgnoredItem() } },
                    ).optionalFieldOf("items", listOf()).forGetter(SacksData::asSackEntries),
                    CodecUtils.list(ItemStack.OPTIONAL_CODEC).optionalFieldOf("ownedSacks").forNullGetter(SacksData::ownedSacks)
                ).apply(it, ::SacksData)
            }
            else -> CodecUtils.unit { SacksData() }
        }
    }

    val counts: Map<SkyBlockId, Int> get() = SACKS.get()?.counts ?: emptyMap()
    val timestamps: Map<SkyBlockId, Instant> get() = SACKS.get()?.timestamps ?: emptyMap()
    val ownedSacks: List<ItemStack> get() = SACKS.get()?.ownedSacks ?: emptyList()

    // Returns the old value
    fun updateItem(item: SkyBlockId, amount: Int): Int {
        if (amount != 0 && item.isIgnoredItem()) return 0
        val oldValue = this.counts[item]
        if (oldValue == amount) return oldValue
        SACKS.get()?.add(item, amount)
        SACKS.save()
        return oldValue ?: 0
    }

    fun updateItemValue(item: SkyBlockId, diff: Int) {
        if (item.isIgnoredItem()) return
        val prevAmount = this.counts[item] ?: 0
        val newAmount = (prevAmount + diff).coerceAtLeast(0)
        updateItem(item, newAmount)
    }

    fun clear() {
        SACKS.get()?.clear()
        SACKS.save()
    }

    fun clearOwnedSacks() {
        SACKS.get()?.ownedSacks?.clear()
        SACKS.save()
    }

    fun setOwnedSacks(sacks: List<ItemStack>) {
        SACKS.edit {
            ownedSacks.replaceWith(sacks)
        }
    }

    private fun String.isIgnoredItem() = SkyBlockId.item(this).isIgnoredItem()
    private fun SkyBlockId.isIgnoredItem(): Boolean = IGNORED_ITEMS.any { it == this }

}
