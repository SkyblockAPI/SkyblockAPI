package tech.thatgravyboat.skyblockapi.api.profile.storage

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.items.storage.PlayerStorageData as NewPlayerStorageData
import tech.thatgravyboat.skyblockapi.api.profile.items.storage.PlayerStorageInstance as NewPlayerStorageInstance
import tech.thatgravyboat.skyblockapi.api.profile.items.storage.StorageData as NewStorageData

@RemoveNextVersion
data class StorageData(
    val normal: PlayerStorageData = PlayerStorageData(),
    val rift: MutableList<PlayerStorageInstance> = mutableListOf(),
) {
    companion object {
        internal fun fromNewData(data: NewStorageData): StorageData {
            return StorageData(
                normal = PlayerStorageData.fromNewData(data.normal),
                rift = data.rift.map(PlayerStorageInstance::fromNewData).toMutableList(),
            )
        }
    }
}

@RemoveNextVersion
data class PlayerStorageData(
    val enderchests: MutableList<PlayerStorageInstance> = mutableListOf(),
    val backpacks: MutableList<PlayerStorageInstance> = mutableListOf(),
) {
    companion object {
        internal fun fromNewData(data: NewPlayerStorageData): PlayerStorageData {
            return PlayerStorageData(
                enderchests = data.enderchests.map(PlayerStorageInstance::fromNewData).toMutableList(),
                backpacks = data.backpacks.map(PlayerStorageInstance::fromNewData).toMutableList(),
            )
        }
    }
}

@RemoveNextVersion
data class PlayerStorageInstance(
    val index: Int = 0,
    val items: MutableList<ItemStack> = mutableListOf(),
) {
    companion object {
        internal fun fromNewData(data: NewPlayerStorageInstance): PlayerStorageInstance {
            return PlayerStorageInstance(
                index = data.index,
                items = data.items,
            )
        }
    }
}
