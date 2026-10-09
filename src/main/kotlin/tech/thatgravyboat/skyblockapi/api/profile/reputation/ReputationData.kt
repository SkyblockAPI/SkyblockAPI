package tech.thatgravyboat.skyblockapi.api.profile.reputation

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class ReputationData(
    var selectedFaction: Faction?,
    val reputation: MutableMap<Faction, Int> = mutableMapOf(),
) {
    public constructor() : this(null)

    public companion object {
        public val CODEC: Codec<ReputationData> = SkyblockAPICodecs.getCodec<ReputationData>()
    }
}
