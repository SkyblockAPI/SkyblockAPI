package tech.thatgravyboat.skyblockapi.api.profile.items.forge

import kotlinx.datetime.Instant
import me.owdding.ktcodecs.GenerateCodec

@GenerateCodec
data class ForgeSlot(
    val id: String,
    val expiryTime: Instant,
)
