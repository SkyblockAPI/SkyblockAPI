package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class TrophyFishData(
    val data: MutableMap<TrophyFishType, MutableMap<TrophyTier, Int>> = mutableMapOf(),
) {
    public companion object {
        public val CODEC: Codec<TrophyFishData> = SkyblockAPICodecs.getCodec<TrophyFishData>()
    }
}
