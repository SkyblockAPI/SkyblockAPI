package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.hub.BazaarAPI
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.Calculator
import tech.thatgravyboat.skyblockapi.api.remote.RepoReforgeStonesAPI
import tech.thatgravyboat.skyblockapi.api.remote.RepoReforgeStonesAPI.getApplyCost

internal object RecombobulatorCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        return BazaarAPI.getProduct("RECOMBOBULATOR_3000")?.sellPrice?.toLong()?.times(stack.getData(DataTypes.RARITY_UPGRADES) ?: 0) ?: 0L
    }
}

internal object ReforgeCalculator : Calculator {
    override fun calculate(stack: ItemStack): Long {
        val reforgeName = stack.getData(DataTypes.MODIFIER) ?: return 0L
        val rarity = stack.getData(DataTypes.RARITY) ?: return 0L
        val stone = RepoReforgeStonesAPI.getReforgeByName(reforgeName) ?: RepoReforgeStonesAPI.getReforge(reforgeName)?.let { reforgeName to it } ?: return 0L

        val applyCost = stone.second.getApplyCost(rarity) ?: 0L

        return BazaarAPI.getProduct(stone.first)?.sellPrice?.toLong()?.plus(applyCost) ?: 0L
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
