package tech.thatgravyboat.skyblockapi.api.profile.quiver

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class QuiverData(
    var current: String?,
    val arrows: MutableMap<String, Int> = mutableMapOf()
) {
    constructor() : this(null)

    companion object {
        val CODEC = SkyblockAPICodecs.getCodec<QuiverData>()
    }
}
