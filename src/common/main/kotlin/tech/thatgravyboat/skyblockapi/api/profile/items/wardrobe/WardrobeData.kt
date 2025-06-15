package tech.thatgravyboat.skyblockapi.api.profile.items.wardrobe

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.KCodec

@GenerateCodec
data class WardrobeData(
    var currentSlot: Int = -1,
    var slots: MutableList<WardrobeSlot> = mutableListOf(),
) {
    companion object {
        val CODEC = KCodec.getCodec<WardrobeData>()
    }
}

