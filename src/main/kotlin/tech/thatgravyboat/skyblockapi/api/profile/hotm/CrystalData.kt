package tech.thatgravyboat.skyblockapi.api.profile.hotm

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.data.CrystalStatus
import tech.thatgravyboat.skyblockapi.api.data.CrystalType
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

// TODO: move somewhere else once its internal in all versions
@GenerateCodec
//~ if >= 26.4 'public' -> 'internal'
public data class CrystalData(
    var crystals: MutableMap<CrystalType, CrystalStatus> = mutableMapOf(),
) {
    public companion object {
        public val CODEC: Codec<CrystalData> = SkyblockAPICodecs.getCodec<CrystalData>()
    }
}
