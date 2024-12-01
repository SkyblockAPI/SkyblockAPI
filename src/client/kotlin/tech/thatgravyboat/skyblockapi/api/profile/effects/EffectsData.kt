package tech.thatgravyboat.skyblockapi.api.profile.effects

import kotlinx.datetime.Instant
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class EffectsData(
    var boosterCookieExpireTime: Instant = Instant.DISTANT_PAST,
) {
    companion object {
        val CODEC = KCodec.getCodec<EffectsData>()
    }
}
