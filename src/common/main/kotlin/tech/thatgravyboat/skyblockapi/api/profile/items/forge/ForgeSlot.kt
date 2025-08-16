package tech.thatgravyboat.skyblockapi.api.profile.items.forge

import me.owdding.ktcodecs.GenerateCodec
import kotlin.time.Instant

@GenerateCodec
data class ForgeSlot(
    val id: String,
    val expiryTime: Instant,
)
