package tech.thatgravyboat.skyblockapi.api.data

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import java.util.UUID

@GenerateCodec
public data class PlayerCacheData(
    val players: MutableMap<UUID, CachedPlayer> = mutableMapOf()
) {
    public companion object {
        public val CODEC: Codec<PlayerCacheData> = SkyblockAPICodecs.getCodec<PlayerCacheData>()
    }
}

@GenerateCodec
public data class CachedPlayer(
    var name: String,
    var time: Long = System.currentTimeMillis()
)
