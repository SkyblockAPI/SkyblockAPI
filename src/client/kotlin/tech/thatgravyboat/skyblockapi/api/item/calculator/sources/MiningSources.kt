package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.Pricing
import tech.thatgravyboat.skyblockapi.api.remote.itemdata.Cost
import tech.thatgravyboat.skyblockapi.api.remote.itemdata.ItemData
import tech.thatgravyboat.skyblockapi.utils.extentions.getId

internal object DrillComponentsCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        val fuelTankPrice = Pricing.getPrice(stack.getData(DataTypes.FUEL_TANK))
        val enginePrice = Pricing.getPrice(stack.getData(DataTypes.ENGINE))
        val upgradeModulePrice = Pricing.getPrice(stack.getData(DataTypes.UPGRADE_MODULE))

        return fuelTankPrice + enginePrice + upgradeModulePrice
    }
}

// TODO: test :3
internal object GemstoneCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        val gemstone = stack.getData(DataTypes.GEMSTONES) ?: return 0L
        val unlockCosts = stack.getId()?.let { ItemData.getItemData(it)?.gemstones } ?: emptyList()
        return gemstone.map { (gem, slot, qu) ->
            val unlockCost = unlockCosts.find { it.slotType == slot }?.cost?.sumOf { Cost.calculateCost(it) } ?: 0L
            val price = BazaarAPI.getProduct("${qu.name}_${gem.name}_GEM")?.sellPrice?.toLong() ?: 0L
            unlockCost + price
        }.sum()
    }
}
