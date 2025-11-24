package tech.thatgravyboat.skyblockapi.api.profile.items.forge

import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import kotlin.time.Instant

@GenerateCodec
data class ForgeSlot(
    val skyBlockId: SkyBlockId,
    val expiryTime: Instant,
) {
    @Deprecated("Use skyBlockId property instead")
    @RemoveNextVersion
    val id get() = skyBlockId.skyblockId
}
