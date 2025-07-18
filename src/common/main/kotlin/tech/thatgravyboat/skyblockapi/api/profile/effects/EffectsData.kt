package tech.thatgravyboat.skyblockapi.api.profile.effects

import kotlinx.datetime.Instant
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import kotlin.time.Duration

@GenerateCodec
data class EffectsData(
    var boosterCookieExpireTime: Instant = Instant.DISTANT_PAST,
    var godPotionDuration: Duration = Duration.ZERO,
) {
    companion object {
        val CODEC = SkyblockAPICodecs.getCodec<EffectsData>()
    }
}
