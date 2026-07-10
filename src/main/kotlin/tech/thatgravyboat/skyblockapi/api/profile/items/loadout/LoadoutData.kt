package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class LoadoutData(
    var armor: WardrobeData = WardrobeData(),
    var equipment: WardrobeData = WardrobeData(),
) {
    companion object {
        val CODEC = SkyblockAPICodecs.getCodec<LoadoutData>()
    }
}
