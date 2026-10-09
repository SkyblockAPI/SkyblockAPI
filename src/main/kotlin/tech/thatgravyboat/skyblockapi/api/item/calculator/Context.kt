package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlotData
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.Cost
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo
import tech.thatgravyboat.skyblockapi.utils.lazy.registryBoundLazy

public sealed interface CalculationEntry {
    public val price: Long
}

public sealed interface ItemLikeEntry : CalculationEntry {
    public val itemId: String
    public val itemStack: ItemStack
    public val skyblockId: SkyBlockId
}

public data class ItemEntry(
    override val itemId: String,
    override val price: Long,
    val amount: Int,
) : ItemLikeEntry {
    override val itemStack by registryBoundLazy { SkyBlockItemsRepo.getItemStackOrDefault(itemId) }
    override val skyblockId: SkyBlockId = SkyBlockId.unknownType(itemId) ?: SkyBlockId.EMPTY

    public constructor(itemId: String) : this(itemId, Pricing.getPrice(itemId), 1)
}

public data class ItemWithLimitEntry(
    override val itemId: String,
    override val price: Long,
    val amount: Int,
    val limit: Int,
) : ItemLikeEntry {
    override val itemStack by registryBoundLazy { SkyBlockItemsRepo.getItemStackOrDefault(itemId) }
    override val skyblockId: SkyBlockId = SkyBlockId.unknownType(itemId) ?: SkyBlockId.EMPTY
}

public data class GroupedEntry(
    val source: ItemValueSource,
    val entries: List<CalculationEntry>,
) : CalculationEntry {
    override val price by lazy { entries.sumOf { it.price } }
}

public data class ReforgeEntry(
    val reforge: String,
    val applyCost: Long,
    override val price: Long,
) : CalculationEntry

public data class CostEntries(
    val cost: List<Cost>,
) : CalculationEntry {
    override val price by lazy { cost.sumOf { Cost.calculateCost(it) } }
}

public data class GemstoneSlotEntry(
    val gemstone: GemstoneSlotData,
    val unlockingCost: CostEntries,
    override val price: Long,
) : ItemLikeEntry {
    override val itemId get() = gemstone.itemId
    override val itemStack by registryBoundLazy { SkyBlockItemsRepo.getItemStackOrDefault(itemId) }
    override val skyblockId: SkyBlockId = gemstone.skyblockId
}

public data class ItemStarEntry(
    val conversionCost: CalculationEntry?,
    val stars: List<CalculationEntry>,
) : CalculationEntry {
    override val price by lazy { (conversionCost?.price ?: 0) + stars.sumOf { it.price } }
}
