package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

private val idLookup = RepoAPI.runes().runes().map { (id, runes) ->
    runes.firstOrNull()?.name?.stripColor()?.substringBeforeLast(" ") to SkyBlockId.rune(id, 0)
}.toMap()

@IdResolvers
internal data object RuneSackIdResolver : InventoryIdResolver {
    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean {
        val titleMatch = menu.title.stripped.endsWith("Runes Sack")
        addDebugString { "Title Match: $titleMatch" }
        return titleMatch
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        val name = this.cleanName
        val lookup = idLookup[name]
        addDebugString { "Lookup $name -> $lookup" }
        return lookup?.asDerived()
    }

    override val priority: Int = 10
}
