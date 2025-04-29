package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.LowestBinAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator

internal object DrillComponentsCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        val fuelTank = stack.getData(DataTypes.FUEL_TANK)
        val engine = stack.getData(DataTypes.ENGINE)
        val upgradeModule = stack.getData(DataTypes.UPGRADE_MODULE)

        val fuelTankPrice = LowestBinAPI.getLowestPrice(fuelTank) ?: 0
        val enginePrice = LowestBinAPI.getLowestPrice(engine) ?: 0
        val upgradeModulePrice = LowestBinAPI.getLowestPrice(upgradeModule) ?: 0

        return fuelTankPrice + enginePrice + upgradeModulePrice
    }
}
