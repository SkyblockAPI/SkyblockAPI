package tech.thatgravyboat.skyblockapi.api.profile.mining.forge

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import kotlin.time.Instant
import tech.thatgravyboat.skyblockapi.api.profile.items.forge.ForgeSlot as NewForgeSlot

@RemoveNextVersion
data class ForgeSlot(
    val id: String,
    val expiryTime: Instant,
) {
    companion object {
        fun fromNewData(data: NewForgeSlot): ForgeSlot = ForgeSlot(
            id = data.id,
            expiryTime = data.expiryTime,
        )
    }
}
