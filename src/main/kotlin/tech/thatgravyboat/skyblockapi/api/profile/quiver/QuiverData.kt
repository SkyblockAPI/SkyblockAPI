package tech.thatgravyboat.skyblockapi.api.profile.quiver

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class QuiverData(
    var current: String?,
    val arrows: MutableMap<String, Int> = mutableMapOf()
) {
    public constructor() : this(null)

    companion object {
        public val CODEC = SkyblockAPICodecs.getCodec<QuiverData>()
    }
}
