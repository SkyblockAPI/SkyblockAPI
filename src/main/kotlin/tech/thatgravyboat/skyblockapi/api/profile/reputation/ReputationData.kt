package tech.thatgravyboat.skyblockapi.api.profile.reputation

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class ReputationData(
    var selectedFaction: Faction?,
    val reputation: MutableMap<Faction, Int> = mutableMapOf(),
) {
    constructor() : this(null)

    companion object {
        val CODEC = SkyblockAPICodecs.getCodec<ReputationData>()
    }
}
