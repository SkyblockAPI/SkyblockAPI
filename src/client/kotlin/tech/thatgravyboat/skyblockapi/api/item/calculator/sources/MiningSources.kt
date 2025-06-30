package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.*
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing

internal object DrillComponentsCalculator : DataTypesCalculator(DataTypes.FUEL_TANK, DataTypes.ENGINE, DataTypes.UPGRADE_MODULE)

internal object GemstoneCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? {
        val gemstone = stack.getData(DataTypes.GEMSTONES) ?: return null
        val unlockCosts = ItemData.getItemData(id)?.gemstones ?: emptyList()

        return gemstone.map { gemstone ->
            GemstoneSlotEntry(
                gemstone,
                CostEntries(unlockCosts.find { it.slotType == gemstone.slot }?.cost ?: emptyList()),
                Pricing.getPrice(gemstone.itemId),
            )
        }

    }
}

internal object SilexCalculator : SingleEntryCalculator {
    const val SILEX = "SIL_EX"
    const val LIMIT = 5
    const val MAX_EFFICIENCY = 5
    val ILLEGAL_ITEMS: Array<String> = arrayOf("STONK_PICKAXE", "PROMISING_SPADE", "PROMISING_AXE", "PROMISING_HOE")

    override fun getEntry(id: String, stack: ItemStack): CalculationEntry? {
        if (id in ILLEGAL_ITEMS) return null
        val efficiency = stack.getData(DataTypes.ENCHANTMENTS)?.get("efficiency") ?: return null
        val amount = (efficiency - MAX_EFFICIENCY).takeIf { it > 0 } ?: return null
        return ItemWithLimitEntry(SILEX, amount * Pricing.getPrice(SILEX), amount, LIMIT)
    }
}

internal object DivanPowderCoatingCalculator : IntDataTypeCalculator(DataTypes.DIVAN_POWDER_COATING, "DIVAN_POWDER_COATING")

internal object PolarVoidCalculator : IntDataTypeWithLimitCalculator(DataTypes.POLARVOID, "POLARVOID_BOOK", 5)

internal object PowerAbilityScrollCalculator : DataTypeCalculator(DataTypes.POWER_ABILITY_SCROLL)
