package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class Loadout(
    var currentSlot: Int = -1,
    var slots: MutableList<LoadoutSlot> = mutableListOf(),
) {
    companion object {
        val CODEC = SkyblockAPICodecs.getCodec<Loadout>()
    }
}
