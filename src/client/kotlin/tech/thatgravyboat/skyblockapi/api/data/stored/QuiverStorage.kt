package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.quiver.QuiverData

internal object QuiverStorage {

    private val QUIVER = StoredProfileData(
        ::QuiverData,
        QuiverData.CODEC,
        "quiver.json"
    )

    val arrows: MutableMap<String, Int>
        get() = QUIVER.get()?.arrows ?: mutableMapOf()

    var currentArrow: String?
        get() = QUIVER.get()?.current
        private set(value) {
            QUIVER.get()?.current = value
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
