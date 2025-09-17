package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlotData
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.Cost
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing

sealed interface CalculationEntry {
    val price: Long
}

sealed interface ItemLikeEntry : CalculationEntry {
    val itemId: String
    val itemStack: ItemStack
    val skyblockId: SkyBlockId
}

data class ItemEntry(
    override val itemId: String,
    override val price: Long,
    val amount: Int,
) : ItemLikeEntry {
    override val itemStack by lazy { RepoItemsAPI.getItem(itemId) }
    override val skyblockId: SkyBlockId = SkyBlockId.unknownType(itemId) ?: SkyBlockId.EMPTY

    constructor(itemId: String) : this(itemId, Pricing.getPrice(itemId), 1)
}

data class ItemWithLimitEntry(
    override val itemId: String,
    override val price: Long,
    val amount: Int,
    val limit: Int,
) : ItemLikeEntry {
    override val itemStack by lazy { RepoItemsAPI.getItem(itemId) }
    override val skyblockId: SkyBlockId = SkyBlockId.unknownType(itemId) ?: SkyBlockId.EMPTY
}

data class GroupedEntry(
    val source: ItemValueSource,
    val entries: List<CalculationEntry>,
) : CalculationEntry {
    override val price by lazy { entries.sumOf { it.price } }
}

data class ReforgeEntry(
    val reforge: String,
    val applyCost: Long,
    override val price: Long,
) : CalculationEntry

data class CostEntries(
    val cost: List<Cost>,
) : CalculationEntry {
    override val price by lazy { cost.sumOf { Cost.calculateCost(it) } }
}

data class GemstoneSlotEntry(
    val gemstone: GemstoneSlotData,
    val unlockingCost: CostEntries,
    override val price: Long,
) : ItemLikeEntry {
    override val itemId get() = gemstone.itemId
    override val itemStack by lazy { RepoItemsAPI.getItem(itemId) }
    override val skyblockId: SkyBlockId = gemstone.skyblockId
}

data class ItemStarEntry(
    val conversionCost: CalculationEntry?,
    val stars: List<CalculationEntry>,
) : CalculationEntry {
    override val price by lazy { (conversionCost?.price ?: 0) + stars.sumOf { it.price } }
}
