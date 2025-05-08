package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.getData
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
    DIVAN_POWDER_COATING(DivanPowderCoatingCalculator),
    POLARVOID(PolarVoidCalculator),
    POWER_ABILITY_SCROLL(PowerAbilityScrollCalculator),
    APPLIED_RUNE(AppliedRuneCalculator),
    APPLIED_DYE(AppliedDyeCalculator),
    HELMET_SKIN(HelmetSkinCalculator),
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

open class DataTypeCalculator(private val dataType: DataType<String>) : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        return Pricing.getPrice(stack.getData(dataType) ?: return 0L)
    }
}

open class DataTypesCalculator(private vararg val dataTypes: DataType<String>) : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        return dataTypes.sumOf { Pricing.getPrice(stack.getData(it) ?: return@sumOf 0L) }
    }
}

open class IntDataTypeCalculator(private val dataType: DataType<Int>, private val itemId: String) : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        return Pricing.getPrice(itemId) * (stack.getData(dataType) ?: 0)
    }
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
