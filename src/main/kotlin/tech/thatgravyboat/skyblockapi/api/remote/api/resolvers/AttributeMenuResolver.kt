package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.ResolutionContext
import tech.thatgravyboat.skyblockapi.api.profile.hunting.AttributeAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.impl.ColoredItems
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.contains

@IdResolvers
internal data object AttributeMenuResolver : InventoryIdResolver {

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean{
        return AttributeAPI.attributeMenuRegex.matches(title)
    }

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        val itemName = this.cleanName
        if (this in ColoredItems.GRAY_DYE) {
            return SimpleItemAPI.findIdByName(itemName)
        }

        return SimpleItemAPI.findIdByName(itemName.substringBeforeLast(" "))?.asDerived()
    }

    override val priority: Int = 10
}
