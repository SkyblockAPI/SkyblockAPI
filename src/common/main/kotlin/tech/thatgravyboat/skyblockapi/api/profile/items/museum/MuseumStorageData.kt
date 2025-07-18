package tech.thatgravyboat.skyblockapi.api.profile.items.museum

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.enumMapOf

@GenerateCodec
data class MuseumStorageData(
    val categories: MutableMap<MuseumCategory, MutableMap<String, MuseumItemData>> = enumMapOf(),
    val armorSets: MutableMap<String, MuseumArmorData> = mutableMapOf(),
    val specialItems: MutableList<ItemStack> = mutableListOf(),
)

@GenerateCodec
data class MuseumArmorData(
    val items: MutableMap<String, ItemStack> = mutableMapOf(),
) {
    inline val inMuseum: Boolean get() = items.isNotEmpty()
}

@GenerateCodec
data class MuseumItemData(
    var item: ItemStack?,
) {
    inline val inMuseum: Boolean get() = item != null
}
