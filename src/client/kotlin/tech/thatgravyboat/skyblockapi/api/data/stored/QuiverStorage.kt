package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredData
import tech.thatgravyboat.skyblockapi.api.profile.profile.ProfileAPI
import tech.thatgravyboat.skyblockapi.api.profile.quiver.QuiverData

internal object QuiverStorage {

    private val QUIVER = StoredData(
        QuiverData(),
        QuiverData.CODEC,
        "quiver.json"
    )

    val arrows: MutableMap<String, Int>
        get() {
            val profileName = ProfileAPI.profileName ?: return mutableMapOf()
            return QUIVER.get().arrows[profileName] ?: mutableMapOf()
        }

    fun updateArrow(id: String, amount: Int) {
        val prevAmount = arrows[id]
        if (prevAmount == amount) return
        arrows[id] = amount
        QUIVER.save()
    }

}
