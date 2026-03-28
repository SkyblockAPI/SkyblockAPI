package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import me.owdding.ktmodules.AutoCollect
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.util.SortedArraySet
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPIIdResolvers
import tech.thatgravyboat.skyblockapi.helpers.McScreen
import java.util.concurrent.atomic.AtomicInteger

interface IdResolver {
    val types: List<IdResolverKind>
    val priority: Int

    fun tryResolve(itemStack: ItemStack, resolverKind: IdResolverKind): SkyBlockId?
}

interface InventoryIdResolver : IdResolver {
    companion object {
        val types = listOf(IdResolverKind.ContainerSlot, IdResolverKind.ContainerContents)
        var priorities = AtomicInteger(10)
    }

    override val types: List<IdResolverKind> get() = InventoryIdResolver.types

    override fun tryResolve(itemStack: ItemStack, resolverKind: IdResolverKind): SkyBlockId? {
        val screen = McScreen.asMenu ?: return null
        return if (itemStack.isApplicable(screen, resolverKind)) itemStack.resolveId(screen, resolverKind) else null
    }

    fun <T : AbstractContainerMenu> ItemStack.isApplicable(menu: AbstractContainerScreen<T>, resolverKind: IdResolverKind): Boolean
    fun <T : AbstractContainerMenu> ItemStack.resolveId(menu: AbstractContainerScreen<T>, resolverKind: IdResolverKind): SkyBlockId?
}

enum class IdResolverKind {
    Equipment,
    Cursor,
    ContainerSlot,
    Inventory,
    EntityData,
    ContainerContents,
    Unknown,
    ;

    private val resolvers: MutableSet<IdResolver> = SortedArraySet.create(Comparator.comparingInt(IdResolver::priority))

    companion object {
        init {
            SkyblockAPIIdResolvers.collected.forEach {
                it.types.forEach { kind ->
                    kind.resolvers.add(it)
                }
            }
        }
    }

    fun entries(): Set<IdResolver> = resolvers
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@AutoCollect
annotation class IdResolvers

