package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.*
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId

enum class ItemValueSource(val calc: Calculator) : Calculator by calc {
    RECOMBOBULATOR(RecombobulatorCalculator),
    REFORGE(ReforgeCalculator),
    ENCHANTMENT(EnchantmentCalculator),
    HOT_POTATO(HotPotatoCalculator),
    ITEM_STARS(ItemStarsCalculator),
    DRILL_COMPONENTS(DrillComponentsCalculator),
    GEMSTONE(GemstoneCalculator),
    FISHING_ROD_PARTS(RodPartCalculator),
    SILEX(SilexCalculator),
    ;

    companion object {
        fun calculate(lowestBin: Long, stack: ItemStack): ItemValueResult {
            val id = stack.getSkyBlockId() ?: return ItemValueResult.EMPTY
            val sources = entries.associateWith { it.calc.calculate(id, stack) }
            return ItemValueResult(lowestBin, (sources.values.sum() + lowestBin) * stack.count, sources)
        }
    }
}

interface Calculator {
    fun calculate(id: String, stack: ItemStack): Long
}

data class ItemValueResult(
    val rawPrice: Long,
    val price: Long,
    val sources: Map<ItemValueSource, Long>,
) {
    companion object {
        @JvmField
        val EMPTY = ItemValueResult(0L, 0L, emptyMap())
    }
}
