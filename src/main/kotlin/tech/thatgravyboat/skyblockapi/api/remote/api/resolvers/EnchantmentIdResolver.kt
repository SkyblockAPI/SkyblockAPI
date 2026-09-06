package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.parseRomanNumeral
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

private val idLookup = RepoAPI.enchantments().enchantments().map { (id, enchantments) ->
    enchantments.name to SkyBlockId.enchantment(id, 0)
}.toMap()

private val nameToIdLookup = RepoAPI.enchantments().enchantments().map { (id, enchantments) ->
    enchantments.name to id
}.toMap()

context(_: ItemDebugCategory)
private fun ItemStack.resolveEnchantedBookId(name: String, level: Int?): SkyBlockId? {
    if (level != null) {
        val id = nameToIdLookup[name] ?: return null
        this.addDebugString { "Name to id: $name -> $id" }
        return SkyBlockId.enchantment(id, level).asDerived()
    }
    return idLookup[name]?.asDerived()
}

@IdResolvers
internal data object EnchantmentTableAndHexEnchantmentIdResolver : InventoryIdResolver {
    private val titleRegex = "(?:The Hex ➜ )?Enchant Item(?: ➜ (.*))?".toRegex()
    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean {
        if (item != Items.ENCHANTED_BOOK) return false
        return (titleRegex.matches(menu.title.stripped))
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(menu: AbstractContainerScreen<T>, resolverKind: IdResolverKind): SkyBlockId? {
        val title = menu.title.stripped
        val isInBuyPage = !titleRegex.find(title)?.groupValues[1].isNullOrEmpty()
        val name = if (isInBuyPage) cleanName.substringBeforeLast(" ") else cleanName
        val level = if (isInBuyPage) cleanName.substringAfterLast(" ").parseRomanNumeral() else null
        addDebugString { "is In Buy Page: $isInBuyPage" }
        return resolveEnchantedBookId(name, level)
    }

    override val priority: Int = 10
}

@IdResolvers
internal data object BazaarEnchantmentIdResolver : InventoryIdResolver {
    private val titleRegex = ("(?:\\(\\d+/\\d+\\) )?(Normal |Ultimate )?Enchantments(?: ➜ .*)?").toRegex()
    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): Boolean {
        val title = menu.title.stripped
        if (!title.contains("➜")) return false
        if (item != Items.ENCHANTED_BOOK) return false

        val name = cleanName.substringBeforeLast(" ").trim()
        val isInBuyPage = title.substringBeforeLast("➜").trim() == name

        return (titleRegex.matches(title) || isInBuyPage)
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(
        menu: AbstractContainerScreen<T>,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        val title = menu.title.stripped
        val name = cleanName.substringBeforeLast(" ").trim()
        val isInBuyPage = title.substringBeforeLast("➜").trim() == name
        val isInNormalOrUltimatePage = !titleRegex.find(title)?.groupValues[1].isNullOrEmpty()
        addDebugString { "Is In Buy Page: $isInBuyPage; Is In Normal/Ultimate Page: $isInNormalOrUltimatePage" }

        val level = if (isInBuyPage || isInNormalOrUltimatePage) cleanName.substringAfterLast(" ").trim().parseRomanNumeral() else null

        return resolveEnchantedBookId(name, level)
    }

    override val priority: Int = 10
}

@IdResolvers
internal data object EnchantmentGuideIdResolver : InventoryIdResolver, ItemDebugCategory {
    private val titleRegex = ".* Enchantments? Guide".toRegex()

    override fun <T : AbstractContainerMenu> ItemStack.isApplicable(menu: AbstractContainerScreen<T>, resolverKind: IdResolverKind): Boolean {
        if (item != Items.ENCHANTED_BOOK) return false
        val matchesTitle = titleRegex.matches(menu.title.stripped)
        this.addDebugString {
            "Title Match: $matchesTitle"
        }
        return matchesTitle
    }

    override fun <T : AbstractContainerMenu> ItemStack.resolveId(menu: AbstractContainerScreen<T>, resolverKind: IdResolverKind): SkyBlockId? {
        val name = this.cleanName.substringBeforeLast(" ").trim()
        val level = this.cleanName.substringAfterLast(" ").trim().toIntValue()
        return resolveEnchantedBookId(name, level)
    }

    override val priority: Int = 10
}
