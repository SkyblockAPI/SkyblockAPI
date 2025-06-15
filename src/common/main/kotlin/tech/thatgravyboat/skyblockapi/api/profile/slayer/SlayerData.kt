package tech.thatgravyboat.skyblockapi.api.profile.slayer

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.generated.KCodec

@GenerateCodec
data class SlayerData(
    var slayers: MutableMap<SlayerType, SlayerEntry> = mutableMapOf(),
) {
    companion object {
        val CODEC = KCodec.getCodec<SlayerData>()
    }
}

@GenerateCodec
data class SlayerEntry(
    var xp: Long = 0L,
    var meterXp: Long = 0L,
)
