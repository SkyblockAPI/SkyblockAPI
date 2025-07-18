package tech.thatgravyboat.skyblockapi.api.data.stored

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.accessory.AccessoryBagItem
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@Module
internal object AccessoryBagStorage {
    private val ACCESSORY_BAG = StoredProfileData(
        { mutableListOf() },
        CodecUtils.mutableList(SkyblockAPICodecs.getCodec<AccessoryBagItem>()),
        "accessory_bag.json",
        true
    )

    fun invalidatePage(page: Int) {
        ACCESSORY_BAG.get()?.removeIf { (_, currentPage) -> currentPage == page }
        ACCESSORY_BAG.save()
    }

    fun addItem(accessoryBagItem: AccessoryBagItem) {
        ACCESSORY_BAG.get()?.add(accessoryBagItem)
        ACCESSORY_BAG.save()
    }

    fun getItems(): List<AccessoryBagItem> = ACCESSORY_BAG.get() ?: emptyList()
}
