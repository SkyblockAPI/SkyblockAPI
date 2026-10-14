package tech.thatgravyboat.skyblockapi.api.profile.slayer

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class SlayerData(
    var slayers: MutableMap<SlayerType, SlayerEntry> = mutableMapOf(),
) {
    public companion object {
        public val CODEC: Codec<SlayerData> = SkyblockAPICodecs.getCodec<SlayerData>()
    }
}

@GenerateCodec
public data class SlayerEntry(
    var xp: Long = 0L,
    var meterXp: Long = 0L,
)
