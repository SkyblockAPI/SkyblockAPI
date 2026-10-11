package tech.thatgravyboat.skyblockapi.api.profile.quiver

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class QuiverData(
    var current: String?,
    val arrows: MutableMap<String, Int> = mutableMapOf()
) {
    public constructor() : this(null)

    public companion object {
        public val CODEC: Codec<QuiverData> = SkyblockAPICodecs.getCodec<QuiverData>()
    }
}
