package tech.thatgravyboat.skyblockapi.api.profile.sacks

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.KCodec

@GenerateCodec
data class SacksData(
    var items: MutableMap<String, Int> = mutableMapOf(),
) {
    companion object {
        val CODEC = KCodec.getCodec<SacksData>()
    }
}
