package tech.thatgravyboat.skyblockapi.api.profile.wardrobe

import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class WardrobeData(
    var currentSlot: Int = -1,
    var slots: MutableList<WardrobeSlot> = mutableListOf(),
) {
    companion object {
        val CODEC = KCodec.getCodec<WardrobeData>()
    }
}

