package tech.thatgravyboat.skyblockapi.api.remote.api

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.EnchantsAPI
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockEnchantmentsRepo

@Deprecated("")
object RepoEnchantmentAPI {

    fun getEnchantmentById(id: String): EnchantsAPI.Enchant? = SkyBlockEnchantmentsRepo.get(id)
    fun getEnchantmentAsItemOrNull(id: String, level: Int? = null): ItemStack? = SkyBlockEnchantmentsRepo.getItemStack {
        this.id = id
        this.level = level
    }
    fun getEnchantmentAsItem(id: String, level: Int? = null): ItemStack = SkyBlockEnchantmentsRepo.getItemStackOrDefault {
        this.id = id
        this.level = level
    }

}
