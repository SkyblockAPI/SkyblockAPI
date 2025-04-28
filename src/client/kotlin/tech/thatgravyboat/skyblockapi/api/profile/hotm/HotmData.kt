package tech.thatgravyboat.skyblockapi.api.profile.hotm

import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class HotmData(
    var perks: MutableMap<String, HotmPerk> = mutableMapOf(),
) {
    companion object {
        val CODEC = KCodec.getCodec<HotmData>()
    }
}

@GenerateCodec
data class HotmPerk(
    val level: Int,
    val unlocked: Boolean,
    val disabled: Boolean,
)
