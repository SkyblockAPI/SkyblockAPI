package tech.thatgravyboat.skyblockapi.api.remote.itemdata

import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlot

@GenerateCodec
data class GemstoneCost(
    @param:FieldName("slot_type") val slotType: GemstoneSlot,
    @FieldName("costs") val cost: List<Cost> = emptyList(),
)
