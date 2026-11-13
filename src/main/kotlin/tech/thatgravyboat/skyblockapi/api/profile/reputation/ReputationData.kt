package tech.thatgravyboat.skyblockapi.api.profile.reputation

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

// TODO: move somewhere else once its internal in all versions
@GenerateCodec
//~ if >= 26.4 'public' -> 'internal'
internal data class ReputationData(
    var selectedFaction: Faction?,
    val reputation: MutableMap<Faction, Int> = mutableMapOf(),
) {
    public constructor() : this(null)

    public companion object {
        public val CODEC: Codec<ReputationData> = SkyblockAPICodecs.getCodec<ReputationData>()
    }
}
