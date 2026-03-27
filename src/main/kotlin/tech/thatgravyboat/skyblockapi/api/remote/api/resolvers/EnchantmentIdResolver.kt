package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.resolvers.InventoryIdResolver.Companion.priorities
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.collections.component1
import kotlin.collections.component2

private val idLookup =  RepoAPI.enchantments().enchantments().map { (id, enchantments) ->
    enchantments.name to SkyBlockId.enchantment(id, 0)
}.toMap()

@IdResolvers
object HexEnchantmentIdResolver : InventoryIdResolver {
    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean {
        return menu.title.stripped == "The Hex ➜ Enchant Item"
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        return idLookup[this.cleanName]
    }

    override val priority: Int = priorities.getAndIncrement()
}
@IdResolvers
object BazzarEnchantmentIdResolver : InventoryIdResolver {
    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean {
        val title = menu.title.stripped
        if (!title.contains("➜")) return false
        return title.substringAfter(")").substringBefore("➜").trim() == "Enchantments"
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        return idLookup[this.cleanName.substringBeforeLast(" ").trim()]
    }

    override val priority: Int = priorities.getAndIncrement()
}
