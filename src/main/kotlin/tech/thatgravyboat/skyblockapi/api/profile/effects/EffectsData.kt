package tech.thatgravyboat.skyblockapi.api.profile.effects

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import kotlin.time.Duration
import kotlin.time.Instant

@GenerateCodec
public data class EffectsData(
    var boosterCookieExpireTime: Instant = Instant.DISTANT_PAST,
    var godPotionDuration: Duration = Duration.ZERO,
) {
    public companion object {
        public val CODEC: Codec<EffectsData> = SkyblockAPICodecs.getCodec<EffectsData>()
    }
}
