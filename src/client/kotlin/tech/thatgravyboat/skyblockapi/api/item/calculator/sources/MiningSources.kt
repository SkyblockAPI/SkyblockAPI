package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.Pricing
import tech.thatgravyboat.skyblockapi.api.remote.itemdata.Cost
import tech.thatgravyboat.skyblockapi.api.remote.itemdata.ItemData

internal object DrillComponentsCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): Long {
        val fuelTankPrice = Pricing.getPrice(stack.getData(DataTypes.FUEL_TANK))
        val enginePrice = Pricing.getPrice(stack.getData(DataTypes.ENGINE))
        val upgradeModulePrice = Pricing.getPrice(stack.getData(DataTypes.UPGRADE_MODULE))

        return fuelTankPrice + enginePrice + upgradeModulePrice
    }
}

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
