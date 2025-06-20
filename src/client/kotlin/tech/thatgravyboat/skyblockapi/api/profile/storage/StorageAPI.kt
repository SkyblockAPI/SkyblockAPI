package tech.thatgravyboat.skyblockapi.api.profile.storage

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.stored.PlayerStorageStorage

@RemoveNextVersion
object StorageAPI {
    val enderchests get(): List<PlayerStorageInstance> = PlayerStorageStorage.enderchests.map { PlayerStorageInstance.fromNewData(it) }
    val backpacks get(): List<PlayerStorageInstance> = PlayerStorageStorage.backpacks.map { PlayerStorageInstance.fromNewData(it) }
    val riftStorage get(): List<PlayerStorageInstance> = PlayerStorageStorage.riftStorage.map { PlayerStorageInstance.fromNewData(it) }
}
