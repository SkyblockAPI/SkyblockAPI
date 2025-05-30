package tech.thatgravyboat.skyblockapi.api.profile.items.sacks

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.KCodec

@GenerateCodec
data class SacksData(
    var items: MutableMap<String, Int> = mutableMapOf(),
) {
    companion object {
        internal val CODEC = KCodec.getCodec<SacksData>()
    }
}
