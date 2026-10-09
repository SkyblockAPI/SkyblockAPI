package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class LoadoutData(
    var armor: WardrobeData = WardrobeData(),
    var equipment: WardrobeData = WardrobeData(),
    var loadouts: Loadout = Loadout(),
) {
    companion object {
        public val CODEC = SkyblockAPICodecs.getCodec<LoadoutData>()
    }
}
