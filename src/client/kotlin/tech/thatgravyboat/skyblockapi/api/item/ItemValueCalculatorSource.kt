package tech.thatgravyboat.skyblockapi.api.item

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.area.hub.LowestBinAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData

enum class ItemValueCalculatorSource(val calc: Calculator) : Calculator by calc {
    RECOMBOBULATOR(RecombobulatorCalculator),
    REFORGE(ReforgeCalculator),
    ENCHANTMENT(EnchantmentCalculator),
    HOT_POTATO(HotPotatoCalculator),
    DRILL_COMPONENTS(DrillComponentsCalculator),
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

object ReforgeCalculator : Calculator {
    // TODO: apply cost, doesnt work on all reforges
    override fun calculate(stack: ItemStack): Long {
        val reforgeName = stack.getData(DataTypes.MODIFIER) ?: return 0L

        return BazaarAPI.getProduct(reforgeName)?.sellPrice?.toLong() ?: 0L
    }
}

object EnchantmentCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        return stack.getData(DataTypes.ENCHANTMENTS)?.map { "ENCHANTMENT_${it.key}_${it.value}" }?.sumOf { enchant ->
            BazaarAPI.getProduct(enchant)?.sellPrice?.toLong() ?: 0L
        } ?: 0L
    }
}

object HotPotatoCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        val applied = stack.getData(DataTypes.HOT_POTATO_BOOKS) ?: return 0L
        val hotPotatoBooks = applied.coerceAtMost(10)
        val fumingBooks = (applied - 10).coerceAtLeast(0)

        val hotPotatoPrice = BazaarAPI.getProduct("HOT_POTATO_BOOK")?.sellPrice?.toLong() ?: 0
        val fumingPrice = BazaarAPI.getProduct("FUMING_POTATO_BOOK")?.sellPrice?.toLong() ?: 0

        return (hotPotatoPrice * hotPotatoBooks) + (fumingPrice * fumingBooks)
    }
}

object DrillComponentsCalculator : Calculator {
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
