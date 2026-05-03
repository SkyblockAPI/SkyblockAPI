package tech.thatgravyboat.skyblockapi.api.remote

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo
import tech.thatgravyboat.skyblockapi.utils.text.Text

@Deprecated("Use SkyBlockItemsRepo instead", ReplaceWith("tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo"))
object RepoItemsAPI {

    fun getItemOrNull(id: String): ItemStack? = SkyBlockItemsRepo.getItemStack(id)
    fun getItem(id: String): ItemStack = SkyBlockItemsRepo.getItemStackOrDefault(id)
    fun getItemOrNullLazy(id: String): Lazy<ItemStack?> = lazy { SkyBlockItemsRepo.getItemStack(id) }
    fun getItemLazy(id: String): Lazy<ItemStack> = lazy { SkyBlockItemsRepo.getItemStackOrDefault(id) }
    fun getItemName(id: String): Component = SkyBlockItemsRepo.getLazyItemStack(id)?.getDisplayName() ?: Text.of("Unknown Item")
    fun getItemIdByName(name: String): String? = SkyBlockItemsRepo.getIdByName(name)
}
