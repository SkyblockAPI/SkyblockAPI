package tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata

import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlot
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec
import tech.thatgravyboat.skyblockapi.modules.FieldName

@GenerateCodec
data class GemstoneCost(
    @param:FieldName("slot_type") val slotType: GemstoneSlot,
    @FieldName("costs") val cost: List<Cost> = emptyList(),
)
