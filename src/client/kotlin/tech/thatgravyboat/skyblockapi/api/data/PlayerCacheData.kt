package tech.thatgravyboat.skyblockapi.api.data

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.KCodec
import java.util.*

@GenerateCodec
data class PlayerCacheData(
    val players: MutableMap<UUID, CachedPlayer> = mutableMapOf()
) {
    companion object {
        val CODEC = KCodec.getCodec<PlayerCacheData>()
    }
}

@GenerateCodec
data class CachedPlayer(
    var name: String,
    var time: Long = System.currentTimeMillis()
)
