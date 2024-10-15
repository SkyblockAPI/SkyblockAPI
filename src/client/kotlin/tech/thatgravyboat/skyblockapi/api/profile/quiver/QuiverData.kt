package tech.thatgravyboat.skyblockapi.api.profile.quiver

import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class QuiverData(
    val profiles: MutableMap<String, ProfileQuiverData> = mutableMapOf(),
) {
    companion object {
        val CODEC = KCodec.getCodec<QuiverData>()
    }
}

@GenerateCodec
data class ProfileQuiverData(
    var current: String? = null,
    val arrows: MutableMap<String, Int> = mutableMapOf()
)
