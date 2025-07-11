package tech.thatgravyboat.skyblockapi.api.item.calculator.sources

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.item.calculator.*
import tech.thatgravyboat.skyblockapi.api.remote.RepoReforgeStonesAPI
import tech.thatgravyboat.skyblockapi.api.remote.RepoReforgeStonesAPI.getApplyCost
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemData
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing

internal object RecombobulatorCalculator : BoolDataTypeCalculator(DataTypes.RECOMBOBULATOR, "RECOMBOBULATOR_3000")

internal object ReforgeCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? {
        val reforgeName = stack.getData(DataTypes.MODIFIER) ?: return null
        val rarity = stack.getData(DataTypes.RARITY) ?: return null
        val (_, stone) =
            RepoReforgeStonesAPI.getReforgeByName(reforgeName)
                ?: RepoReforgeStonesAPI.getReforge(reforgeName)?.let { reforgeName to it }
                ?: return null

        return listOf(
            ReforgeEntry(
                stone.name,
                stone.getApplyCost(rarity) ?: 0,
                Pricing.getPrice(stone.name),
            ),
        )
    }
}

internal object EnchantmentCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? {
        val enchants = stack.getData(DataTypes.ENCHANTMENTS) ?: return null
        return enchants.map { "ENCHANTMENT_${it.key}_${it.value}" }.map { ItemEntry(it) }
    }
}

internal object HotPotatoCalculator : Calculator {
    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? {
        val applied = stack.getData(DataTypes.HOT_POTATO_BOOKS) ?: return null
        val hotPotatoBooks = applied.coerceAtMost(10)
        val fumingBooks = (applied - 10).coerceAtLeast(0)

        return listOf(
            ItemWithLimitEntry(
                "HOT_POTATO_BOOK",
                Pricing.getPrice("HOT_POTATO_BOOK") * hotPotatoBooks,
                hotPotatoBooks,
                10,
            ),
            ItemWithLimitEntry(
                "FUMING_POTATO_BOOK",
                Pricing.getPrice("FUMING_POTATO_BOOK") * fumingBooks,
                fumingBooks,
                5,
            ),
        )
    }
}

internal object ArtOfWarCalculator : BoolDataTypeCalculator(DataTypes.ART_OF_WAR, "THE_ART_OF_WAR")

internal object ArtOfPeaceCalculator : BoolDataTypeCalculator(DataTypes.ART_OF_PEACE, "THE_ART_OF_PEACE")

internal object BoostersCalculator : DataTypeListCalculator(DataTypes.BOOSTERS)

internal object JalapenoBookCalculator : BoolDataTypeCalculator(DataTypes.JALAPENO_BOOK, "JALAPENO_BOOK")

internal object ItemStarsCalculator : Calculator {
    val masterStars = listOf(
        "FIRST",
        "SECOND",
        "THIRD",
        "FOURTH",
        "FIFTH",
    ).map { "${it}_MASTER_STAR" }

    override fun calculate(id: String, stack: ItemStack): List<CalculationEntry>? {
        val stars = stack.getData(DataTypes.STAR_COUNT) ?: return null

        val data = ItemData.getItemData(id)
        val starCost = data?.upgradeCost?.take(stars) ?: emptyList()
        val conversionCost = data?.conversionCost?.let { CostEntries(listOf(it)) }

        return if (stack.getData(DataTypes.CATEGORY)?.isDungeon == true) {
            val dungeonStars = stars.coerceAtMost(5)
            val masterStars = (stars - dungeonStars).coerceAtLeast(0)

            ItemStarEntry(
                conversionCost,
                buildList {
                    starCost.forEach {
                        add(CostEntries(it))
                    }

                    ItemStarsCalculator.masterStars.take(masterStars).forEach {
                        add(ItemEntry(it))
                    }
                },
            )
        } else {
            ItemStarEntry(conversionCost, starCost.map { CostEntries(it) })
        }.let(::listOf)
    }
}

internal object NecronScrollsCalculator : DataTypeListCalculator(DataTypes.NECRON_SCROLLS)
