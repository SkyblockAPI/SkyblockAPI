package tech.thatgravyboat.skyblockapi.api.area.isle.trophyfish

import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class TrophyFishData(
    val data: MutableMap<TrophyFishType, MutableMap<TrophyFishTier, Int>> = mutableMapOf(),
) {
    companion object {
        val CODEC = KCodec.getCodec<TrophyFishData>()
    }
}
