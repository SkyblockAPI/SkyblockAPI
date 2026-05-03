package tech.thatgravyboat.skyblockapi.api.remote.api

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.repolib.api.AttributesAPI
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockAttributesRepo

@Deprecated("Use SkyBlockAttributesRepo instead", ReplaceWith("tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockAttributesRepo"))
object RepoAttributeAPI {

    fun getAttributeDataById(id: String): AttributesAPI.Attribute? = SkyBlockAttributesRepo.get(id)
    fun getAttributeByIdOrNull(id: String): ItemStack? = SkyBlockAttributesRepo.getItemStack(id)

}
