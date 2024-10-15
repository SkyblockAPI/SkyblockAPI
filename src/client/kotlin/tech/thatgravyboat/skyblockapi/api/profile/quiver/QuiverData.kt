package tech.thatgravyboat.skyblockapi.api.profile.quiver

import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class QuiverData(
    var current: String?,
    val arrows: MutableMap<String, Int> = mutableMapOf()
) {
    constructor() : this(null)

    companion object {
        val CODEC = KCodec.getCodec<QuiverData>()
    }
}
