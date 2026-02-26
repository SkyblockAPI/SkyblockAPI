package tech.thatgravyboat.skyblockapi.api.profile.items.museum

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.enumMapOf

@GenerateCodec
data class MuseumStorageData(
    var milestone: Int = 0,
    val categories: MutableMap<MuseumCategory, MutableMap<SkyBlockId, MuseumItemData>> = enumMapOf(),
    val specialItems: MutableList<ItemStack> = mutableListOf(),
)

@GenerateCodec
data class MuseumItemData(
    var item: ItemStack?,
) {
    inline val inMuseum: Boolean get() = item != null
}
