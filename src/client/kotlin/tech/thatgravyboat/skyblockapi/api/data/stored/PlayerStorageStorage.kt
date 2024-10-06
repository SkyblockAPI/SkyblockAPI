package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.api.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.api.profile.storage.PlayerStorageData
import tech.thatgravyboat.skyblockapi.api.profile.storage.PlayerStorageInstance
import tech.thatgravyboat.skyblockapi.api.profile.storage.StorageData

internal object PlayerStorageStorage {

    private val PLAYER_STORAGE = StoredData(
        StorageData(),
        StorageData.CODEC,
        StoredData.defaultPath.resolve("player_storage.json"),
    )

    private val normalStorage: PlayerStorageData
        get() = PLAYER_STORAGE.get().normal.getOrPut(ProfileAPI.profileName ?: "") {
            PlayerStorageData()
        }

    val riftStorage: MutableList<PlayerStorageInstance>
        get() = PLAYER_STORAGE.get().rift.getOrPut(ProfileAPI.profileName ?: "") {
            mutableListOf()
        }

    val enderchests: MutableList<PlayerStorageInstance>
        get() = normalStorage.enderchests

    val backpacks: MutableList<PlayerStorageInstance>
        get() = normalStorage.backpacks

    fun setEnderchest(data: PlayerStorageInstance) {
        val old = this.enderchests.find { it.index == data.index }
        if (old == null) {
            this.enderchests.add(data)
            PLAYER_STORAGE.save()
        } else if (data != old) {
            val index = this.enderchests.indexOf(old)
            this.enderchests[index] = data
            PLAYER_STORAGE.save()
        }
    }

    fun setBackpack(data: PlayerStorageInstance) {
        val old = this.backpacks.find { it.index == data.index }
        if (old == null) {
            this.backpacks.add(data)
            PLAYER_STORAGE.save()
        } else if (data != old) {
            val index = this.backpacks.indexOf(old)
            this.backpacks[index] = data
            PLAYER_STORAGE.save()
        }
    }

    fun setRiftStorage(data: PlayerStorageInstance) {
        val old = this.riftStorage.find { it.index == data.index }
        if (old == null) {
            this.riftStorage.add(data)
            PLAYER_STORAGE.save()
        } else if (data != old) {
            val index = this.riftStorage.indexOf(old)
            this.riftStorage[index] = data
            PLAYER_STORAGE.save()
        }
    }
}
