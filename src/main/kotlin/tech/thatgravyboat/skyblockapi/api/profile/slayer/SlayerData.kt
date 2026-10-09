package tech.thatgravyboat.skyblockapi.api.profile.slayer

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

@GenerateCodec
public data class SlayerData(
    var slayers: MutableMap<SlayerType, SlayerEntry> = mutableMapOf(),
) {
    companion object {
        public val CODEC = SkyblockAPICodecs.getCodec<SlayerData>()
    }
}

@GenerateCodec
public data class SlayerEntry(
    var xp: Long = 0L,
    var meterXp: Long = 0L,
)
