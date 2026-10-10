//? < 26.2 {
/*package tech.thatgravyboat.skyblockapi.api.remote

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.PetsAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockPetsRepo

@Deprecated("")
@Suppress("DEPRECATION")
public object RepoPetsAPI {

    public fun getPetInfo(id: String): PetsAPI.Data? = SkyBlockPetsRepo.get(id)
    public fun getPetAsItemOrNull(query: PetQuery): ItemStack? = SkyBlockPetsRepo.getItemStack {
        this.id = query.id
        this.rarity = query.rarity
        this.level = query.level
        this.skin = query.skin
        this.heldItem = query.heldItem
    }
    public fun getPetAsItem(id: String, rarity: SkyBlockRarity, level: Int = 100, skin: String? = null, heldItem: String? = null): ItemStack = SkyBlockPetsRepo.getItemStackOrDefault {
        this.id = id
        this.rarity = rarity
        this.level = level
        this.skin = skin
        this.heldItem = heldItem
    }
    public fun getPetAsItem(query: PetQuery): ItemStack = SkyBlockPetsRepo.getItemStackOrDefault {
        this.id = query.id
        this.rarity = query.rarity
        this.level = query.level
        this.skin = query.skin
        this.heldItem = query.heldItem
    }
}

@Deprecated("")
public data class PetQuery(
    val id: String,
    val rarity: SkyBlockRarity,
    val level: Int,
    val skin: String? = null,
    val heldItem: String? = null,
)*///?}
