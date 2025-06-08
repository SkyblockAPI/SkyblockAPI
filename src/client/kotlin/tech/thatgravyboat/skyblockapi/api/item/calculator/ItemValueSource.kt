package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.*
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId

enum class ItemValueSource(val calc: Calculator) : Calculator by calc {
    RECOMBOBULATOR(RecombobulatorCalculator),
    REFORGE(ReforgeCalculator),
    ENCHANTMENT(EnchantmentCalculator),
    HOT_POTATO(HotPotatoCalculator),
    ART_OF_WAR(ArtOfWarCalculator),
    ART_OF_PEACE(ArtOfPeaceCalculator),
    JALAPENO_BOOK(JalapenoBookCalculator),
    NECRON_SCROLLS(NecronScrollsCalculator),
    ITEM_STARS(ItemStarsCalculator),
    DRILL_COMPONENTS(DrillComponentsCalculator),
    GEMSTONE(GemstoneCalculator),
    FISHING_ROD_PARTS(RodPartCalculator),
    SILEX(SilexCalculator),
    DIVAN_POWDER_COATING(DivanPowderCoatingCalculator),
    POLARVOID(PolarVoidCalculator),
    POWER_ABILITY_SCROLL(PowerAbilityScrollCalculator),
    BOOK_OF_STATS(BookOfStatsCalculator),
    APPLIED_RUNE(AppliedRuneCalculator),
    APPLIED_DYE(AppliedDyeCalculator),
    HELMET_SKIN(HelmetSkinCalculator),
    ;

    companion object {
        fun calculate(lowestBin: Long, stack: ItemStack): ItemValueResult {
            val id = stack.getSkyBlockId() ?: return ItemValueResult.EMPTY
            val sources = entries.associateWith { it.calc.calculate(id, stack) }.mapNotNull { (key, value) -> value?.let { GroupedEntry(key, value) } }
            return ItemValueResult(
                lowestBin,
                lowestBin + sources.sumOf { it.price } * stack.count,
                sources.associate { it.source to it.price },
                sources,
            )
        }
    }
}

data class ItemValueResult(
    val rawPrice: Long,
    val price: Long,
    @RemoveNextVersion val sources: Map<ItemValueSource, Long>,
    val entryTree: List<GroupedEntry>,
) {
    companion object {
        @JvmField
        val EMPTY = ItemValueResult(0L, 0L, emptyMap(), listOf())
    }
}

