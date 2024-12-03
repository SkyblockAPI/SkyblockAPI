package tech.thatgravyboat.skyblockapi.api.profile.effects

import kotlinx.datetime.Instant
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec
import kotlin.time.Duration

@GenerateCodec
data class EffectsData(
    var boosterCookieExpireTime: Instant = Instant.DISTANT_PAST,
    var godPotionDuration: Duration = Duration.ZERO,
) {
    companion object {
        val CODEC = KCodec.getCodec<EffectsData>()
    }
}
