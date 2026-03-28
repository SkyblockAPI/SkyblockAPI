package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.CommonComponents
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.api.SimpleItemAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.fromItem
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.fromName
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@IdResolvers
object DefaultIdResolver : IdResolver {
    override val types: List<IdResolverKind> = listOf(IdResolverKind.Unknown)
    override val priority: Int = Int.MIN_VALUE

    override fun tryResolve(
        itemStack: ItemStack,
        resolverKind: IdResolverKind,
    ): SkyBlockId? {
        val itemId = fromItem(itemStack)
        if (itemId != null) return itemId

        // Used for ignoring same names on things like dyes and barriers where it is usually important to keep it as no id.
        // i.e. anvil with no items has a barrier named 'Anvil'
        if (itemStack.item in ItemTag.IGNORE_NAME_LOOKUP) return null

        // If names are the same as their vanilla counterpart then ignore as this is likely just a UI item.
        // i.e. ender chest icon in storage
        //? >= 26.1 {
        if (itemStack.item.components().getOrDefault(DataComponents.ITEM_NAME, CommonComponents.EMPTY).stripped.equals(itemStack.hoverName.stripped, true)) return null
        //? } else
        //if (itemStack.item.name.stripped.equals(itemStack.hoverName.stripped, true)) return null

        val nameId = fromName(itemStack.hoverName.stripped, false) ?: return null

        // An item may not be available if it can't be parsed, in this case we will just return the id we suspect.
        val repoItem = SimpleItemAPI.getUnknownById(nameId) ?: return nameId

        return if (repoItem.item == itemStack.item) nameId else null
    }
}
