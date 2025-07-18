package tech.thatgravyboat.skyblockapi.api.profile.items.sacks

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class SacksData(
    var items: MutableMap<String, Int> = mutableMapOf(),
) {
    companion object {
        internal val CODEC = SkyblockAPICodecs.getCodec<SacksData>()
    }
}
