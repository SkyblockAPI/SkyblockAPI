package tech.thatgravyboat.skyblockapi.api.profile.community

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
data class CommunityCenterData(
    var rank: FameRank = FameRanks.NEW_PLAYER,
    val bitsAvailable: MutableMap<String, Long> = mutableMapOf(),
) {
    companion object {
        val CODEC: Codec<CommunityCenterData> = SkyblockAPICodecs.getCodec<CommunityCenterData>()
    }
}
