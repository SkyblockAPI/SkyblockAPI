package tech.thatgravyboat.skyblockapi.api.data

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import java.util.UUID

@GenerateCodec
data class PlayerCacheData(
    val players: MutableMap<UUID, CachedPlayer> = mutableMapOf()
) {
    companion object {
        val CODEC = SkyblockAPICodecs.getCodec<PlayerCacheData>()
    }
}

@GenerateCodec
data class CachedPlayer(
    var name: String,
    var time: Long = System.currentTimeMillis()
)
