package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import me.owdding.ktmodules.AutoCollect
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.util.SortedArraySet
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPIIdResolvers
import tech.thatgravyboat.skyblockapi.helpers.McScreen

interface IdResolver {
    val types: List<IdResolverKind>
    val priority: Int

    fun tryResolve(itemStack: ItemStack, resolverKind: IdResolverKind): SkyBlockId?
}

interface InventoryIdResolver : IdResolver {
    companion object {
        val types = listOf(IdResolverKind.ContainerSlot, IdResolverKind.ContainerContents)
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

    private val resolvers: MutableSet<IdResolver> = LinkedHashSet()

    companion object {
        init {
            SkyblockAPIIdResolvers.collected.forEach {
                it.types.forEach { kind ->
                    if (kind == Unknown) {
                        for (resolverKind in entries) {
                            resolverKind.resolvers.add(it)
                        }
                        return@forEach
                    }
                    kind.resolvers.add(it)
                }
            }
            IdResolverKind.entries.forEach {
                it.resolvers.toSortedSet(Comparator.comparingInt(IdResolver::priority).reversed().thenComparing(Comparator.comparingInt(IdResolver::hashCode))).apply {
                    it.resolvers.clear()
                    it.resolvers.addAll(this)
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

