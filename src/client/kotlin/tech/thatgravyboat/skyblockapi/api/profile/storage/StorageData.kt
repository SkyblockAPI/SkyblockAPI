package tech.thatgravyboat.skyblockapi.api.profile.storage

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class StorageData(
    val normal: MutableMap<String, PlayerStorageData> = mutableMapOf(),
    val rift: MutableMap<String, MutableList<PlayerStorageInstance>> = mutableMapOf()
) {

    companion object {
        val CODEC = KCodec.getCodec<StorageData>()
    }
}

@GenerateCodec
data class PlayerStorageData(
    val enderchests: MutableList<PlayerStorageInstance> = mutableListOf(),
    val backpacks: MutableList<PlayerStorageInstance> = mutableListOf()
)

@GenerateCodec
data class PlayerStorageInstance(
    val index: Int = 0,
    val items: MutableList<ItemStack> = mutableListOf()
)
