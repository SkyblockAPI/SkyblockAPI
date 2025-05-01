package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.*

enum class ItemValueSource(val calc: Calculator) : Calculator by calc {
    RECOMBOBULATOR(RecombobulatorCalculator),
    REFORGE(ReforgeCalculator),
    ENCHANTMENT(EnchantmentCalculator),
    HOT_POTATO(HotPotatoCalculator),
    DRILL_COMPONENTS(DrillComponentsCalculator),
    FISHING_ROD_PARTS(RodPartCalculator),
    ;

    companion object {
        fun calculate(lowestBin: Long, stack: ItemStack): ItemValueResult {
            val sources = entries.associateWith { it.calc.calculate(stack) }
            return ItemValueResult(lowestBin, sources.values.sum() + lowestBin, sources)
        }
    }
}

interface Calculator {
    fun calculate(stack: ItemStack): Long
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
