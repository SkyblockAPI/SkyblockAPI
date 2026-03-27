package tech.thatgravyboat.skyblockapi.api.remote.api.resolvers

import me.owdding.ktmodules.AutoCollect
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.helpers.McScreen

interface IdResolver {
    val types: List<IdResolverKind>
    val priority: Int

    fun ItemStack.tryResolve(): SkyBlockId?

}

@Suppress("NOTHING_TO_INLINE")
inline fun IdResolver.tryResolve(stack: ItemStack, idResolverKind: IdResolverKind): SkyBlockId? = stack.tryResolve()

interface BaseIdResolver : IdResolver {
    fun ItemStack.isApplicable(): Boolean
    fun ItemStack.resolveId(): SkyBlockId?

    override fun ItemStack.tryResolve(): SkyBlockId? {
        return if (isApplicable()) resolveId() else null
    }
}

interface InventoryIdResolver : IdResolver {
    companion object {
        val types = listOf(IdResolverKind.ContainerSlot, IdResolverKind.ContainerContents)
    }

    override val types: List<IdResolverKind> get() = InventoryIdResolver.types

    override fun ItemStack.tryResolve(): SkyBlockId? {
        val screen = McScreen.asMenu ?: return null
        return if (isApplicable(screen)) resolveId() else null
    }

    fun <T : AbstractContainerMenu> ItemStack.isApplicable(menu: AbstractContainerScreen<T>): Boolean
    fun ItemStack.resolveId(): SkyBlockId?

}

enum class IdResolverKind {
    Equipment,
    Cursor,
    ContainerSlot,
    Inventory,
    EntityData,
    ContainerContents,
    Unknown,
}

@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
@AutoCollect
annotation class IdResolvers

