package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

private val idLookup = RepoAPI.runes().runes().map { (id, runes) ->
    runes.firstOrNull()?.name?.stripColor()?.substringBeforeLast(" ") to SkyBlockId.rune(id, 0)
}.toMap()

@IdResolvers
internal object RuneSackIdResolver : InventoryIdResolver {
    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean {
        return menu.title.stripped == "Runes Sack"
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        return idLookup[this.cleanName]?.asDerived()
    }

    override val priority: Int = 10
}
