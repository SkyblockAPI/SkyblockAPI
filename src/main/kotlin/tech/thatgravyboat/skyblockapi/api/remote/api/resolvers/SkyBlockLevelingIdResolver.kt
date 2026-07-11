package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog.TrophyFrogType
import tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish.TrophyFishType
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.TrophyTier
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@IdResolvers
internal data object RockMilestonesResolver : InventoryIdResolver {
    override val priority: Int = 10

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean = menu.title.stripped == "Mining ➜ Rock Milestones"

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        val rarity = SkyBlockRarity.fromNameOrNull(this.cleanName.substringBefore(" ")) ?: return null
        return SkyBlockId.pet("rock", rarity).asDerived()
    }
}

@IdResolvers
internal data object TrophyFishResolver : InventoryIdResolver {
    override val priority: Int = 10

    val idLookup = TrophyFishType.entries.flatMap {
        buildList {
            val stripped = it.displayName.stripped
            add(stripped to it.getId(TrophyTier.DIAMOND))
            addAll(TrophyTier.entries.map { tier ->
                "Fish ${tier.displayName.stripColor()} $stripped" to it.getId(tier)
            })
        }
    }.toMap()

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean = menu.title.stripped == "Fishing ➜ Trophy Fish" || menu.title.stripped.startsWith("Trophy Fish ➜ ")

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
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

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean = menu.title.stripped == "Fishing ➜ Trophy Frogs" || menu.title.stripped.startsWith("Trophy Frogs ➜ ")

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        return idLookup[this.cleanName]?.asDerived()
    }
}

@IdResolvers
internal data object DolphinMilestonesResolver : InventoryIdResolver {
    override val priority: Int = 10

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean = menu.title.stripped == "Fishing ➜ Dolphin Milestones"

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        val rarity = SkyBlockRarity.fromNameOrNull(this.cleanName.substringBefore(" ")) ?: return null
        return SkyBlockId.pet("dolphin", rarity).asDerived()
    }
}
