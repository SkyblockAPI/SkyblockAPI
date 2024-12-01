package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.effects.EffectsData

internal object EffectsStorage {
    private val EFFECTS = StoredProfileData(
        ::EffectsData,
        EffectsData.CODEC,
        "effects.json",
    )

    var boosterCookieExpireTime: Long
        get() = EFFECTS.get()?.boosterCookieExpireTime ?: 0
        set(value) {
            if (EFFECTS.get()?.boosterCookieExpireTime == value) return
            EFFECTS.get()?.boosterCookieExpireTime = value
            EFFECTS.save()
        }

}

