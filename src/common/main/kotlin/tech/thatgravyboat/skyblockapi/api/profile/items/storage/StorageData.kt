package tech.thatgravyboat.skyblockapi.api.profile.items.storage

import me.owdding.ktcodecs.GenerateCodec
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class StorageData(
    val normal: PlayerStorageData = PlayerStorageData(),
    val rift: MutableList<PlayerStorageInstance> = mutableListOf(),
) {
    companion object {
        internal val CODEC = SkyblockAPICodecs.getCodec<StorageData>()
    }
}

@GenerateCodec
data class PlayerStorageData(
    val enderchests: MutableList<PlayerStorageInstance> = mutableListOf(),
    val backpacks: MutableList<PlayerStorageInstance> = mutableListOf(),
)

@GenerateCodec
data class PlayerStorageInstance(
    val index: Int = 0,
    val items: MutableList<ItemStack> = mutableListOf(),
)
