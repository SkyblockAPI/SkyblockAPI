//? < 26.2 {
/*package tech.thatgravyboat.skyblockapi.api.remote

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo
import tech.thatgravyboat.skyblockapi.utils.text.Text

@Deprecated("Use SkyBlockItemsRepo instead", ReplaceWith("tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo"))
public object RepoItemsAPI {

    public fun getItemOrNull(id: String): ItemStack? = SkyBlockItemsRepo.getItemStack(id)
    public fun getItem(id: String): ItemStack = SkyBlockItemsRepo.getItemStackOrDefault(id)
    public fun getItemOrNullLazy(id: String): Lazy<ItemStack?> = lazy { SkyBlockItemsRepo.getItemStack(id) }
    public fun getItemLazy(id: String): Lazy<ItemStack> = lazy { SkyBlockItemsRepo.getItemStackOrDefault(id) }
    public fun getItemName(id: String): Component = SkyBlockItemsRepo.getLazyItemStack(id)?.getDisplayName() ?: Text.of("Unknown Item")
    public fun getItemIdByName(name: String): String? = SkyBlockItemsRepo.getIdByName(name)
}*///?}
