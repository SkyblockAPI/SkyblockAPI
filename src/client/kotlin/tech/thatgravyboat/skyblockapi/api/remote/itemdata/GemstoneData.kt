package tech.thatgravyboat.skyblockapi.api.remote.itemdata

import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlot
import tech.thatgravyboat.skyblockapi.kcodec.GenerateCodec
import tech.thatgravyboat.skyblockapi.modules.FieldName

@GenerateCodec
data class GemstoneCost(
    @param:FieldName("slot_type") val slotType: GemstoneSlot,
    val cost: List<Cost> = emptyList(),
)
