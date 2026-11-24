package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.datatype.ResolutionContext
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor

private val idLookup = RepoAPI.runes().runes().map { (id, runes) ->
    runes.firstOrNull()?.name?.stripColor()?.substringBeforeLast(" ") to SkyBlockId.rune(id, 0)
}.toMap()

@IdResolvers
internal data object RuneSackIdResolver : InventoryIdResolver {

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean = title.endsWith("Runes Sack")

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        val name = this.cleanName
        val lookup = idLookup[name]
        addDebugString { "Lookup $name -> $lookup" }
        return lookup?.asDerived()
    }

    override val priority: Int = 10
}
