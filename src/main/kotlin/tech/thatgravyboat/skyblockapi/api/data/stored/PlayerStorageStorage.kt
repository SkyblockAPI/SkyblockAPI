package tech.thatgravyboat.skyblockapi.api.data.stored

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.storage.PlayerStorageData
import tech.thatgravyboat.skyblockapi.api.profile.items.storage.PlayerStorageInstance
import tech.thatgravyboat.skyblockapi.api.profile.items.storage.StorageData
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant

@Module
internal object PlayerStorageStorage {

    private val PLAYER_STORAGE = StoredProfileData(
        ::StorageData,
        StorageData.CODEC,
        "player_storage.json",
        true
    )

    private inline val storage: StorageData? get() = PLAYER_STORAGE.get()

    private inline val normalStorage: PlayerStorageData?
        get() = storage?.normal

    val riftStorage: MutableList<PlayerStorageInstance>
        get() = storage?.rift ?: mutableListOf()

    val enderchests: MutableList<PlayerStorageInstance>
        get() = normalStorage?.enderchests ?: mutableListOf()

    val backpacks: MutableList<PlayerStorageInstance>
        get() = normalStorage?.backpacks ?: mutableListOf()

    fun setEnderchest(data: PlayerStorageInstance) = normalStorage?.enderchests?.setStorageInstance(data)

    fun setBackpack(data: PlayerStorageInstance) = normalStorage?.backpacks?.setStorageInstance(data)

    fun setRiftStorage(data: PlayerStorageInstance) = storage?.rift?.setStorageInstance(data)

    private fun MutableList<PlayerStorageInstance>.setStorageInstance(data: PlayerStorageInstance) {
        val old = find { it.index == data.index }
        data.lastUpdate = currentInstant()
        if (old == null) {
            add(data)
            PLAYER_STORAGE.save()
        } else if (data != old) {
            val index = indexOf(old)
            this[index] = data
            PLAYER_STORAGE.save()
        }
    }
}
