package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec

@GenerateCodec
data class MaxwellTuning(
    val stat: SkyBlockStat,
    val value: Double,
)
