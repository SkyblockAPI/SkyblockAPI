package tech.thatgravyboat.skyblockapi.api.profile.community

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class CommunityCenterData(
    var rank: FameRank = FameRanks.NEW_PLAYER,
    var gems: Long = 0,
    val bitsAvailable: MutableMap<String, Long> = mutableMapOf(),
) {
    public companion object {
        public val CODEC: Codec<CommunityCenterData> = SkyblockAPICodecs.getCodec<CommunityCenterData>()
    }
}
