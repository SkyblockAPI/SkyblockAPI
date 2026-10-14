package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class LoadoutData(
    var armor: WardrobeData = WardrobeData(),
    var equipment: WardrobeData = WardrobeData(),
    var loadouts: Loadout = Loadout(),
) {
    public companion object {
        public val CODEC: Codec<LoadoutData> = SkyblockAPICodecs.getCodec<LoadoutData>()
    }
}
