package tech.thatgravyboat.skyblockapi.api.data.stored

import kotlinx.datetime.Instant
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.effects.EffectsData
import kotlin.math.abs
import kotlin.time.Duration

internal object EffectsStorage {
    private val EFFECTS = StoredProfileData(
        ::EffectsData,
        EffectsData.CODEC,
        "effects.json",
    )

    var boosterCookieExpireTime: Instant
        get() = EFFECTS.get()?.boosterCookieExpireTime ?: Instant.DISTANT_PAST
        set(value) {
            val current = EFFECTS.get()?.boosterCookieExpireTime
            if (current != null && abs(current.toEpochMilliseconds() - value.toEpochMilliseconds()) < 500) return
            EFFECTS.get()?.boosterCookieExpireTime = value
            EFFECTS.save()
        }

    var godPotionDuration: Duration
        get() = EFFECTS.get()?.godPotionDuration ?: Duration.ZERO
        set(value) {
            if (value == EFFECTS.get()?.godPotionDuration) return
            EFFECTS.get()?.godPotionDuration = value
            EFFECTS.save()
        }

}

