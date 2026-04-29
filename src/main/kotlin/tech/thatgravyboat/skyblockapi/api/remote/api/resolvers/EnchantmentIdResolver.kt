package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

private val idLookup = RepoAPI.enchantments().enchantments().map { (id, enchantments) ->
    enchantments.name to SkyBlockId.enchantment(id, 0)
}.toMap()

private val nameToIdLookup = RepoAPI.enchantments().enchantments().map { (id, enchantments) ->
    enchantments.name to id
}.toMap()

@IdResolvers
internal object EnchantmentTableAndHexEnchantmentIdResolver : InventoryIdResolver {
    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean {
        return menu.title.stripped == "The Hex ➜ Enchant Item" || menu.title.stripped == "Enchant Item"
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        return idLookup[this.cleanName]?.asDerived()
    }

    override val priority: Int = 10
}

@IdResolvers
internal object BazaarEnchantmentIdResolver : InventoryIdResolver {
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
        return idLookup[this.cleanName.substringBeforeLast(" ").trim()]?.asDerived()
    }

    override val priority: Int = 10
}

@IdResolvers
internal object EnchantmentGuideIdResolver : InventoryIdResolver {
    private val titleRegex = ".* Enchantments Guide".toRegex()

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(menu: AbstractContainerScreen<T>, resolverKind: IdResolverKind): Boolean {
        return titleRegex.matches(menu.title.stripped)
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(menu: AbstractContainerScreen<T>, resolverKind: IdResolverKind): SkyBlockId? {
        val id = nameToIdLookup[this.cleanName.substringBeforeLast(" ").trim()] ?: return null
        val level = this.cleanName.substringAfterLast(" ").trim().toIntValue()
        return SkyBlockId.enchantment(id, level)?.asDerived()
    }

    override val priority: Int = 10
}
