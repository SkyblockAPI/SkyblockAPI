package tech.thatgravyboat.skyblockapi.api.profile.sacks

import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class SacksData(
    var items: MutableMap<String, Int> = mutableMapOf(),
) {
    companion object {
        val CODEC = KCodec.getCodec<SacksData>()
    }
}
