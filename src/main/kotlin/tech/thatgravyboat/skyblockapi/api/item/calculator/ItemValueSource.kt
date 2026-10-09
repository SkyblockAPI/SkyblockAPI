package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.AppliedDyeCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.AppliedRuneCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.ArtOfPeaceCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.ArtOfWarCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.BaseItemSource
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.BookOfStatsCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.BoostersCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.DivanPowderCoatingCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.DrillComponentsCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.EnchantmentCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.EnrichmentCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.GemstoneCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.HelmetSkinCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.HotPotatoCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.ItemStarsCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.JalapenoBookCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.NecronScrollsCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.OverclockerCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.PolarVoidCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.PowerAbilityScrollCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.RecombobulatorCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.ReforgeCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.RodPartCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.SilexCalculator
import tech.thatgravyboat.skyblockapi.api.item.calculator.sources.WetBookCalculator
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId

enum class ItemValueSource(val calc: Calculator) : Calculator by calc {
    BASE_ITEM(BaseItemSource),
    RECOMBOBULATOR(RecombobulatorCalculator),
    REFORGE(ReforgeCalculator),
    ENCHANTMENT(EnchantmentCalculator),
    HOT_POTATO(HotPotatoCalculator),
    ART_OF_WAR(ArtOfWarCalculator),
    ART_OF_PEACE(ArtOfPeaceCalculator),
    JALAPENO_BOOK(JalapenoBookCalculator),
    BOOSTERS(BoostersCalculator),
    NECRON_SCROLLS(NecronScrollsCalculator),
    ITEM_STARS(ItemStarsCalculator),
    DRILL_COMPONENTS(DrillComponentsCalculator),
    GEMSTONE(GemstoneCalculator),
    FISHING_ROD_PARTS(RodPartCalculator),
    WET_BOOK(WetBookCalculator),
    SILEX(SilexCalculator),
    DIVAN_POWDER_COATING(DivanPowderCoatingCalculator),
    POLARVOID(PolarVoidCalculator),
    POWER_ABILITY_SCROLL(PowerAbilityScrollCalculator),
    BOOK_OF_STATS(BookOfStatsCalculator),
    APPLIED_RUNE(AppliedRuneCalculator),
    APPLIED_DYE(AppliedDyeCalculator),
    HELMET_SKIN(HelmetSkinCalculator),
    ENRICHMENT(EnrichmentCalculator),
    OVERCLOCKER(OverclockerCalculator)
    ;

    companion object {
        fun calculate(lowestBin: Long, stack: ItemStack): ItemValueResult {
            val id = stack.getSkyBlockId() ?: return ItemValueResult.EMPTY
            val sources = entries.associateWith { it.calc.calculate(id, stack) }.mapNotNull { (key, value) -> value?.let { GroupedEntry(key, value) } }
            return ItemValueResult(
                lowestBin,
                sources.sumOf { it.price } * stack.count,
                sources,
            )
        }
    }
}

data class ItemValueResult(
    val rawPrice: Long,
    val price: Long,
    val entryTree: List<GroupedEntry>,
) {
    companion object {
        @JvmField
        val EMPTY = ItemValueResult(0L, 0L, listOf())
    }
}

