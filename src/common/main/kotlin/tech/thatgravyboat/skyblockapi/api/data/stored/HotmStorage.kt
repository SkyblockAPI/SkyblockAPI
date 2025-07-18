package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotm.HotmData
import tech.thatgravyboat.skyblockapi.api.profile.hotm.HotmPerk

internal object HotmStorage {

    private val HOTM = StoredProfileData(
        ::HotmData,
        HotmData.CODEC,
        "hotm.json",
    )

    var perks: MutableMap<String, HotmPerk>
        get() = HOTM.get()?.perks ?: mutableMapOf()
        private set(value) {
            HOTM.get()?.perks = value
            HOTM.save()
        }

    fun setPerk(name: String, perk: HotmPerk) {
        if (perks[name] == perk) return
        perks[name] = perk
        HOTM.save()
    }

}
