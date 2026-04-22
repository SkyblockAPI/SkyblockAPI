package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.contains
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@IdResolvers
object AttributeMenuResolver : InventoryIdResolver {

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean {
        return menu.title.stripped == "Attribute Menu"
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        val itemName = this.cleanName
        if (this in Items.GRAY_DYE) {
            return SimpleItemAPI.findIdByName(itemName)
        }

        return SimpleItemAPI.findIdByName(itemName.substringBeforeLast(" "))?.asFake()
    }

    override val priority: Int = 10
}
