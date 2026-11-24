package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

// TODO: move somewhere else once its internal in all versions
@GenerateCodec
//~ if >= 26.4 'public' -> 'internal'
public data class LoadoutData(
    var armor: WardrobeData = WardrobeData(),
    var equipment: WardrobeData = WardrobeData(),
    var loadouts: Loadout = Loadout(),
) {
    public companion object {
        public val CODEC: Codec<LoadoutData> = SkyblockAPICodecs.getCodec<LoadoutData>()
    }
}
