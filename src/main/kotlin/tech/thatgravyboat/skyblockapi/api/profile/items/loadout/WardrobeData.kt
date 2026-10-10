package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class WardrobeData(
    var currentSlot: Int = -1,
    var slots: MutableList<WardrobeSlot> = mutableListOf(),
) {
    public companion object {
        public val CODEC: Codec<WardrobeData> = SkyblockAPICodecs.getCodec<WardrobeData>()
    }
}

