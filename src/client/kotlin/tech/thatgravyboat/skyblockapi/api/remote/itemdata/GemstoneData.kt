package tech.thatgravyboat.skyblockapi.api.remote.itemdata

import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlot

@Deprecated("Moved to remote.hypixel.itemdata")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
data class GemstoneCost(
    @param:FieldName("slot_type") val slotType: GemstoneSlot,
    @FieldName("costs") val cost: List<Cost> = emptyList(),
)
