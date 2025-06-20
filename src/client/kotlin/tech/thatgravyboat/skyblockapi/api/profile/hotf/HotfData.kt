package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
data class HotfData(
    val perks: MutableMap<String, HotfPerk> = mutableMapOf(),
    var tokens: Int = 0,
    var whispers: Long = 0,
)

@GenerateCodec
data class HotfPerk(
    val level: Int,
    val unlocked: Boolean,
    val disabled: Boolean,
)
