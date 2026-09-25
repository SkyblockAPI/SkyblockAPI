package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.ResolutionContext
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

private val petLookup = RepoAPI.pets().pets().map { (id, pet) ->
    pet.name to id
}.toMap()

@IdResolvers
internal data object OringoIdResolver : InventoryIdResolver {
    override val priority: Int = 10

    private const val TITLE = "Oringo - Traveling Zookeeper"

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.isApplicable(): Boolean = title == TITLE

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    override fun ItemStack.resolveId(): SkyBlockId? {
        val lastSibling = hoverName.siblings.lastOrNull()
        addDebugString { "Last Sibling: '${lastSibling?.string}', Color: ${lastSibling?.color}" }

        val color = lastSibling?.color ?: return null
        val rarity = SkyBlockRarity.fromColorOrNull(color) ?: run {
            addDebugString { "Rarity lookup failed for color '$color'" }
            return null
        }

        val petName = cleanName.substringAfter("] ")
        val id = petLookup[petName] ?: run {
            addDebugString { "Pet repo lookup failed for '$petName'" }
            return null
        }
        return SkyBlockId.pet(id, rarity).asDerived()
    }
}
