package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec(createCodecMethod = true)
data class HotfData(
    val perks: MutableMap<String, HotfPerk> = mutableMapOf(),
    var tokens: Int = 0,
    var forest: Long = 0,
    var forestTotal: Long = 0,
)

@GenerateCodec
data class HotfPerk(
    val level: Int,
    val unlocked: Boolean,
    val disabled: Boolean,
)
