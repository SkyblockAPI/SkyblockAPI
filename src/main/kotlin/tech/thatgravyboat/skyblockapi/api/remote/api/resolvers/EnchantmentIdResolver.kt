package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.datatype.ResolutionContext
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.parseRomanNumeral
import tech.thatgravyboat.skyblockapi.utils.extentions.splitOnLast
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.extentions.trim

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

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean = item == Items.ENCHANTED_BOOK

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        val match = titleRegex.matchEntire(title) ?: return null
        val isInBuyPage = match.groupValues[1].isNotEmpty()
        val cleanName = this.cleanName

        val (name, level) = if (isInBuyPage) {
            val (name, romanLevel) = cleanName.splitOnLast(" ")
            name to romanLevel.parseRomanNumeral()
        } else cleanName to null

        addDebugString { "is In Buy Page: $isInBuyPage" }
        return resolveEnchantedBookId(name, level)
    }

    override val priority: Int = 10
}

@IdResolvers
internal data object BazaarEnchantmentIdResolver : InventoryIdResolver {
    private val titleRegex = ("(?:\\(\\d+/\\d+\\) )?(Normal |Ultimate )?Enchantments(?: ➜ .*)?").toRegex()

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean {
        return item == Items.ENCHANTED_BOOK && title.contains('➜')
    }

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        val cleanName = this.cleanName
        val (name, romanLevel) = cleanName.splitOnLast(" ").trim()
        val isInBuyPage = title.substringBeforeLast("➜").trim() == name
        val match = titleRegex.matchEntire(title)
        if (!isInBuyPage && match == null) return null
        val isInNormalOrUltimatePage = !match?.groupValues[1].isNullOrEmpty()
        addDebugString { "Is In Buy Page: $isInBuyPage; Is In Normal/Ultimate Page: $isInNormalOrUltimatePage" }

        val level = if (isInBuyPage || isInNormalOrUltimatePage) romanLevel.parseRomanNumeral() else null

        return resolveEnchantedBookId(name, level)
    }

    override val priority: Int = 10
}

@IdResolvers
internal data object EnchantmentGuideIdResolver : InventoryIdResolver, ItemDebugCategory {
    private val titleRegex = ".* Enchantments? Guide".toRegex()

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean {
        return item == Items.ENCHANTED_BOOK && titleRegex.matches(title)
    }

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        val cleanName = this.cleanName
        val (name, levelString) = cleanName.splitOnLast(" ").trim()
        val level = levelString.toIntValue()
        return resolveEnchantedBookId(name, level)
    }

    override val priority: Int = 10
}
