package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.ResolutionContext
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName

@IdResolvers
internal data object BaitSackIdResolver : InventoryIdResolver {

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean = title.endsWith("Bait Sack")

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        val id = context[ResolutionContext.Resolver.ID]

        if (id.equals("common_hook", ignoreCase = true)) {
            addDebugString { "Is Common Hook" }
            return SkyBlockId.fromName(this.cleanName)?.asDerived()
        }

        return null
    }

    override val priority: Int = Int.MAX_VALUE
}
