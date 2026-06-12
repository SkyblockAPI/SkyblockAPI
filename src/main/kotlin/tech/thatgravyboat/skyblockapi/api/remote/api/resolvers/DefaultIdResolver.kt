package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.fromItem
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.impl.debug.addStringDebug

@IdResolvers
data object DefaultIdResolver : IdResolver {

    override val types: List<IdResolverKind> = listOf(IdResolverKind.Unknown)
    override val priority: Int = Int.MAX_VALUE

    override fun tryResolve(itemStack: ItemStack, resolverKind: IdResolverKind): SkyBlockId? {
        return fromItem(itemStack)
    }
}
