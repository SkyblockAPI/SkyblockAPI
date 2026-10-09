package tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class TrophyFrogData(
    val data: MutableMap<TrophyFrogType, MutableMap<TrophyTier, Int>> = mutableMapOf(),
) {
    companion object {
        public val CODEC = SkyblockAPICodecs.getCodec<TrophyFrogData>()
    }
}
