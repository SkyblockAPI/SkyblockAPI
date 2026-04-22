package tech.thatgravyboat.skyblockapi.api.remote.api

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.EnchantsAPI
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockEnchantmentRepo

@Deprecated("")
object RepoEnchantmentAPI {

    fun getEnchantmentById(id: String): EnchantsAPI.Enchant? = SkyBlockEnchantmentRepo.get(id)
    fun getEnchantmentAsItemOrNull(id: String, level: Int? = null): ItemStack? = SkyBlockEnchantmentRepo.getItemStack {
        this.id = id
        this.level = level
    }
    fun getEnchantmentAsItem(id: String, level: Int? = null): ItemStack = SkyBlockEnchantmentRepo.getItemStackOrDefault {
        this.id = id
        this.level = level
    }

}
