package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
data class HotfPerk(
    val level: Int,
    val unlocked: Boolean,
    val disabled: Boolean,
)
