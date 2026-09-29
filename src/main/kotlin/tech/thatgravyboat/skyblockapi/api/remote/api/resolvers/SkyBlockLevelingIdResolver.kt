package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog.TrophyFrogType
import tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish.TrophyFishType
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.api.datatype.ResolutionContext
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@IdResolvers
internal data object RockMilestonesResolver : InventoryIdResolver {
    override val priority: Int = 10

    private const val TITLE = "Mining ➜ Rock Milestones"

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean = title == TITLE

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        val rarity = SkyBlockRarity.fromNameOrNull(this.cleanName.substringBefore(" ")) ?: return null
        return SkyBlockId.pet("rock", rarity).asDerived()
    }
}

@IdResolvers
internal data object TrophyFishResolver : InventoryIdResolver {
    override val priority: Int = 10

    private val idLookup = TrophyFishType.entries.flatMap {
        buildList {
            val stripped = it.displayName.stripped
            add(stripped to it.getId(TrophyTier.DIAMOND))
            addAll(TrophyTier.entries.map { tier ->
                "Fish ${tier.displayName.stripColor()} $stripped" to it.getId(tier)
            })
        }
    }.toMap()

    private val titleRegex = "(?:➜ )?Trophy Fish(?: ➜)?".toRegex()

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean = titleRegex.containsMatchIn(title)

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        return idLookup[this.cleanName]?.asDerived()
    }
}

@IdResolvers
internal data object TrophyFrogResolver : InventoryIdResolver {
    override val priority: Int = 10

    val idLookup = TrophyFrogType.entries.flatMap {
        buildList {
            val stripped = it.displayName.stripped
            add(stripped to it.getId(TrophyTier.DIAMOND))
            addAll(TrophyTier.entries.map { tier ->
                "Frog ${tier.displayName.stripColor()} $stripped" to it.getId(tier)
            })
        }
    }.toMap()

    private val titleRegex = "(?:➜ )?Trophy Frogs(?: ➜)?".toRegex()

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean = titleRegex.containsMatchIn(title)

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        return idLookup[this.cleanName]?.asDerived()
    }
}

@IdResolvers
internal data object DolphinMilestonesResolver : InventoryIdResolver {
    override val priority: Int = 10

    private const val TITLE = "Fishing ➜ Dolphin Milestones"

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean = title == TITLE

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        val rarity = SkyBlockRarity.fromNameOrNull(this.cleanName.substringBefore(" ")) ?: return null
        return SkyBlockId.pet("dolphin", rarity).asDerived()
    }
}
