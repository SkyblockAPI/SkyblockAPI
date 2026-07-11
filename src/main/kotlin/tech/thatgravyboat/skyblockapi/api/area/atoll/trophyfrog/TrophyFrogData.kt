package tech.thatgravyboat.skyblockapi.api.area.atoll.trophyfrog

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.TrophyTier
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class TrophyFrogData(
    val data: MutableMap<TrophyFrogType, MutableMap<TrophyTier, Int>> = mutableMapOf(),
) {
    companion object {
        val CODEC = SkyblockAPICodecs.getCodec<TrophyFrogData>()
    }
}
