package tech.thatgravyboat.skyblockapi.api.data.stored

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

public object VaultStorage {
    private val ACCESSORY_BAG = StoredProfileData(
        { mutableListOf() },
        CodecUtils.mutableList(SkyblockAPICodecs.getCodec<ItemStack>()),
        "vault.json",
    )

    public fun invalidate() {
        ACCESSORY_BAG.get()?.clear()
        ACCESSORY_BAG.save()
    }

    public fun addItem(item: ItemStack) {
        ACCESSORY_BAG.get()?.add(item)
        ACCESSORY_BAG.save()
    }

    public fun getItems(): List<ItemStack> = ACCESSORY_BAG.get() ?: emptyList()
}
