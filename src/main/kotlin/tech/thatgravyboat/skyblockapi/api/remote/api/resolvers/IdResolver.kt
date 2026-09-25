package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import me.owdding.ktmodules.AutoCollect
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.datatype.ResolutionContext
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPIIdResolvers
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.utils.extentions.replaceWith
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

internal interface IdResolver : ItemDebugCategory {
    val types: List<IdResolverKind>
    val priority: Int

    fun tryResolve(itemStack: ItemStack, context: ResolutionContext, resolverKind: IdResolverKind): SkyBlockId?
}

internal interface InventoryIdResolver : IdResolver {
    companion object {
        val types = listOf(IdResolverKind.ContainerSlot, IdResolverKind.ContainerContents)
    }

    override val types: List<IdResolverKind> get() = InventoryIdResolver.types

    override fun tryResolve(itemStack: ItemStack, context: ResolutionContext, resolverKind: IdResolverKind): SkyBlockId? {
        val screen = McScreen.asMenu ?: run {
            itemStack.addDebugString { "Unable to resolve due to no menu" }
            return null
        }
        val menu = screen.menu
        val slot = menu.slots.find { ItemStack.isSameItemSameComponents(it.item, itemStack) } ?: return null
        val containerSlotCount = menu.slots.size - 36
        context(screen, screen.title.stripped, context, resolverKind) {
            return if (slot.index < containerSlotCount && itemStack.isApplicable()) itemStack.resolveId() else null
        }
    }

    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    fun ItemStack.isApplicable(): Boolean
    context(menu: AbstractContainerScreen<*>, title: String, context: ResolutionContext, resolverKind: IdResolverKind)
    fun ItemStack.resolveId(): SkyBlockId?
}

internal enum class IdResolverKind {
    Equipment,
    Cursor,
    ContainerSlot,
    Inventory,
    EntityData,
    ContainerContents,
    Unknown,
    ;

    private val resolvers: MutableSet<IdResolver> = LinkedHashSet()

    @Module
    companion object {
        init {
            SkyblockAPIIdResolvers.collected.forEach { resolver ->
                resolver.types.forEach { kind ->
                    if (kind != Unknown) kind.resolvers.add(resolver)
                    else entries.forEach { it.resolvers.add(resolver) }
                }
            }
            IdResolverKind.entries.forEach {
                val sorted = it.resolvers.sortedWith(Comparator.comparingInt(IdResolver::priority).reversed())
                it.resolvers.replaceWith(sorted)
            }
        }
    }

    fun entries(): Set<IdResolver> = resolvers
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@AutoCollect
internal annotation class IdResolvers

