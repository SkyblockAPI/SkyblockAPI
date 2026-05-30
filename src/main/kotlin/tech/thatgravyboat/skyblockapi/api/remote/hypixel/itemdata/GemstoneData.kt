package tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata

import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlot

@GenerateCodec
data class GemstoneCost(
    @FieldName("slot_type") @EnumFallback(GemstoneSlot.UNKNOWN) val slotType: GemstoneSlot,
    @FieldName("costs") val cost: List<Cost> = emptyList(),
)
