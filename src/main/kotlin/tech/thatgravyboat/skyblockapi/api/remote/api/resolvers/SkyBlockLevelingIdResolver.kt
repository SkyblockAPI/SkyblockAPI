package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.resolvers.InventoryIdResolver.Companion.priorities
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@IdResolvers
internal object RockMilestonesResolver : InventoryIdResolver {
    override val priority: Int get() = priorities.getAndIncrement()

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean = menu.title.stripped == "Mining ➜ Rock Milestones"

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        val rarity = SkyBlockRarity.fromNameOrNull(this.cleanName.substringBefore(" ")) ?: return null
        return SkyBlockId.pet("rock", rarity)
    }
}

@IdResolvers
internal object TrophyFishResolver : InventoryIdResolver {
    override val priority: Int get() = priorities.getAndIncrement()

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean = menu.title.stripped == "Fishing ➜ Trophy Fish"

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        // TODO
        return null
    }
}

@IdResolvers
internal object DolphinMilestonesResolver : InventoryIdResolver {
    override val priority: Int get() = priorities.getAndIncrement()

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean = menu.title.stripped == "Fishing ➜ Dolphin Milestones"

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        val rarity = SkyBlockRarity.fromNameOrNull(this.cleanName.substringBefore(" ")) ?: return null
        return SkyBlockId.pet("dolphin", rarity)
    }
}
