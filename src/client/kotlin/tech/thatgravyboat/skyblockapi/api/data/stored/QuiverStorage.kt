package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.api.profile.quiver.ProfileQuiverData
import tech.thatgravyboat.skyblockapi.api.profile.quiver.QuiverData

internal object QuiverStorage {

    private val QUIVER = StoredData(
        QuiverData(),
        QuiverData.CODEC,
        "quiver.json"
    )

    private inline val profile: ProfileQuiverData?
        get() {
            return QUIVER.get().profiles.getOrPut(ProfileAPI.profileName ?: return null, ::ProfileQuiverData)
        }

    val arrows: MutableMap<String, Int>
        get() = profile?.arrows ?: mutableMapOf()

    var currentArrow: String?
        get() = profile?.current
        private set(value) {
            profile?.current = value
        }


    fun updateArrow(id: String, amount: Int) {
        val prevAmount = arrows[id]
        if (prevAmount == amount) return
        arrows[id] = amount
        QUIVER.save()
    }

    fun updateCurrent(id: String) {
        if (id == currentArrow) return
        currentArrow = id
        QUIVER.save()
    }

    fun updateAll(map: Map<String, Int>) {
        if (arrows == map) return
        arrows.clear()
        arrows.putAll(map)
        QUIVER.save()
    }
}
