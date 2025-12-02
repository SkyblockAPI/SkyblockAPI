package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
data class PowderData(
    val mithril: PowderInfo = PowderInfo(),
    val gemstone: PowderInfo = PowderInfo(),
    val glacite: PowderInfo = PowderInfo(),
)

@GenerateCodec
data class PowderInfo(
    var current: Long = 0,
    var total: Long = 0,
)
