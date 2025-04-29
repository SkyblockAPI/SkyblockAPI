package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator

internal object RecombobulatorCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        return BazaarAPI.getProduct("RECOMBOBULATOR_3000")?.sellPrice?.toLong()?.times(stack.getData(DataTypes.RARITY_UPGRADES) ?: 0) ?: 0L
    }
}

internal object ReforgeCalculator : Calculator {
    // TODO: apply cost, doesnt work on all reforges
    override fun calculate(stack: ItemStack): Long {
        val reforgeName = stack.getData(DataTypes.MODIFIER) ?: return 0L

        return BazaarAPI.getProduct(reforgeName)?.sellPrice?.toLong() ?: 0L
    }
}

internal object EnchantmentCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        return stack.getData(DataTypes.ENCHANTMENTS)?.map { "ENCHANTMENT_${it.key}_${it.value}" }?.sumOf { enchant ->
            BazaarAPI.getProduct(enchant)?.sellPrice?.toLong() ?: 0L
        } ?: 0L
    }
}

internal object HotPotatoCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        val applied = stack.getData(DataTypes.HOT_POTATO_BOOKS) ?: return 0L
        val hotPotatoBooks = applied.coerceAtMost(10)
        val fumingBooks = (applied - 10).coerceAtLeast(0)

        val hotPotatoPrice = BazaarAPI.getProduct("HOT_POTATO_BOOK")?.sellPrice?.toLong() ?: 0
        val fumingPrice = BazaarAPI.getProduct("FUMING_POTATO_BOOK")?.sellPrice?.toLong() ?: 0

        return (hotPotatoPrice * hotPotatoBooks) + (fumingPrice * fumingBooks)
    }
}
