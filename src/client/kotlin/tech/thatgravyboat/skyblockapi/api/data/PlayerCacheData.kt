package tech.thatgravyboat.skyblockapi.api.data

import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec
import java.util.*

@GenerateCodec
data class PlayerCacheData(
    val players: MutableMap<UUID, CachedPlayer> = mutableMapOf()
) {
    companion object {
        val CODED = KCodec.getCodec<PlayerCacheData>()
    }
}

data class CachedPlayer(
    var name: String,
    var time: Long = System.currentTimeMillis()
)
