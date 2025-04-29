package tech.thatgravyboat.skyblockapi.api.item

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData

enum class ItemValueCalculatorSource(val calc: Calculator) : Calculator by calc {
    RECOMBOBULATOR(RecombobulatorCalculator),
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
    val sources: Map<ItemValueCalculatorSource, Long>,
) {
    companion object {
        @JvmStatic
        val EMPTY = ItemValueResult(0L, 0L, emptyMap())
    }
}

object RecombobulatorCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        return BazaarAPI.getProduct("RECOMBOBULATOR_3000")?.sellPrice?.toLong()?.times(stack.getData(DataTypes.RARITY_UPGRADES) ?: 0) ?: 0L
    }
}
