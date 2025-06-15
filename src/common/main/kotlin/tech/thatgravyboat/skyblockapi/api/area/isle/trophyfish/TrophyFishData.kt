package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.KCodec

@GenerateCodec
data class TrophyFishData(
    val data: MutableMap<TrophyFishType, MutableMap<TrophyFishTier, Int>> = mutableMapOf(),
) {
    companion object {
        val CODEC = KCodec.getCodec<TrophyFishData>()
    }
}
