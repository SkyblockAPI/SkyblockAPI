package tech.thatgravyboat.skyblockapi.api.profile.community

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class CommunityCenterData(
    var rank: FameRank = FameRanks.NEW_PLAYER,
    val bitsAvailable: MutableMap<String, Long> = mutableMapOf(),
) {
    companion object {
        val CODEC: Codec<CommunityCenterData> = KCodec.getCodec<CommunityCenterData>()
    }
}
