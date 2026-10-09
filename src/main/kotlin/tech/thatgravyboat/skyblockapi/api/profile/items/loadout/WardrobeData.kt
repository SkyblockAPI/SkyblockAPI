package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class WardrobeData(
    var currentSlot: Int = -1,
    var slots: MutableList<WardrobeSlot> = mutableListOf(),
) {
    companion object {
        public val CODEC = SkyblockAPICodecs.getCodec<WardrobeData>()
    }
}

