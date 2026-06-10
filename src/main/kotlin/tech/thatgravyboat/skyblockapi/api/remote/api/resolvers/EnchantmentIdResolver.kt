package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
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
internal data object EnchantmentTableAndHexEnchantmentIdResolver : InventoryIdResolver {
    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean {
        val stripped = menu.title.stripped
        val isHex = stripped == "The Hex ➜ Enchant Item"
        val isTable = stripped == "Enchant Item"

        addDebugString { "Is Hex: $isHex; Is Table: $isTable" }
        return isHex || isTable
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        val name = this.cleanName
        val lookup = idLookup[name]
        this.addDebugString { "Name to id: $name -> $lookup" }
        return lookup?.asDerived()
    }

    override val priority: Int = 10
}

@IdResolvers
internal data object BazaarEnchantmentIdResolver : InventoryIdResolver {
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
internal data object EnchantmentGuideIdResolver : InventoryIdResolver, ItemDebugCategory {
    private val titleRegex = ".* Enchantments Guide".toRegex()

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(menu: AbstractContainerScreen<T>, resolverKind: IdResolverKind): Boolean {
        val matchesTitle = titleRegex.matches(menu.title.stripped)
        this.addDebugString {
            "Title Match: $matchesTitle"
        }
        return matchesTitle
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(menu: AbstractContainerScreen<T>, resolverKind: IdResolverKind): SkyBlockId? {
        val name = this.cleanName.substringBeforeLast(" ").trim()
        val lookup = nameToIdLookup[name]
        this.addDebugString { "Name to id: $name -> $lookup" }
        val id = lookup ?: return null
        val level = this.cleanName.substringAfterLast(" ").trim().toIntValue()
        return SkyBlockId.enchantment(id, level).asDerived()
    }

    override val priority: Int = 10
}
