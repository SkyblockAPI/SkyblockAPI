package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.DataTypeCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.DataTypesCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.IntDataTypeCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.Pricing
import tech.thatgravyboat.skyblockapi.api.remote.itemdata.Cost
import tech.thatgravyboat.skyblockapi.api.remote.itemdata.ItemData

internal object DrillComponentsCalculator : DataTypesCalculator(DataTypes.FUEL_TANK, DataTypes.ENGINE, DataTypes.UPGRADE_MODULE)

internal object GemstoneCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        val gemstone = stack.getData(DataTypes.GEMSTONES) ?: return 0L
        val unlockCosts = ItemData.getItemData(id)?.gemstones ?: emptyList()
        return gemstone.sumOf { (gem, slot, qu) ->
            val unlockCost = unlockCosts.find { it.slotType == slot }?.cost?.sumOf { Cost.calculateCost(it) } ?: 0L
            val price = Pricing.getPrice("${qu.name}_${gem.name}_GEM")
            unlockCost + price
        }
    }
}

internal object SilexCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        if (id == "STONK_PICKAXE") return 0L
        val efficiency = stack.getData(DataTypes.ENCHANTMENTS)?.get("efficiency") ?: return 0L
        return (efficiency - 5).coerceAtLeast(0) * Pricing.getPrice("SIL_EX")
    }
}

internal object DivanPowderCoatingCalculator : IntDataTypeCalculator(DataTypes.DIVAN_POWDER_COATING, "DIVAN_POWDER_COATING")

internal object PolarVoidCalculator : IntDataTypeCalculator(DataTypes.POLARVOID, "POLARVOID_BOOK")

internal object PowerAbilityScrollCalculator : DataTypeCalculator(DataTypes.POWER_ABILITY_SCROLL)
