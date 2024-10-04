package tech.thatgravyboat.skyblockapi.api.profile

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec
import java.util.*

@GenerateCodec
data class CommunityCenterData(
    val ranks: MutableMap<UUID, FameRank> = mutableMapOf(),
    val bitsAvailable: MutableMap<String, Long> = mutableMapOf(),
) {
    companion object {
        val CODEC: Codec<CommunityCenterData> = KCodec.getCodec<CommunityCenterData>()
    }
}
