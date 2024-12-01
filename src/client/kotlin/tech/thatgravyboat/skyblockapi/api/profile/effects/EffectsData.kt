package tech.thatgravyboat.skyblockapi.api.profile.effects

import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class EffectsData(
    var boosterCookieExpireTime: Long = 0,
) {
    companion object {
        val CODEC = KCodec.getCodec<EffectsData>()
    }
}
