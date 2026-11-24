package tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class TrophyFrogData(
    val data: MutableMap<TrophyFrogType, MutableMap<TrophyTier, Int>> = mutableMapOf(),
) {
    public companion object {
        public val CODEC: Codec<TrophyFrogData> = SkyblockAPICodecs.getCodec<TrophyFrogData>()
    }
}
