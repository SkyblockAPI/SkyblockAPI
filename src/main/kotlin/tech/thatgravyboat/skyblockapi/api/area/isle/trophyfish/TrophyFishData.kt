package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.trophy.TrophyTier
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class TrophyFishData(
    val data: MutableMap<TrophyFishType, MutableMap<TrophyTier, Int>> = mutableMapOf(),
) {
    companion object {
        val CODEC = SkyblockAPICodecs.getCodec<TrophyFishData>()
    }
}
