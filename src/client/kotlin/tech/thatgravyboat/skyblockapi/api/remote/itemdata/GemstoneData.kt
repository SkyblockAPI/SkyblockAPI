package tech.thatgravyboat.skyblockapi.api.remote.itemdata

import me.owdding.ktcodecs.FieldName
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GemstoneSlot

@RemoveNextVersion(
    ReplaceWith(
        "GemstoneCost",
        "tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata",
    ),
)
data class GemstoneCost(
    @param:FieldName("slot_type") val slotType: GemstoneSlot,
    @FieldName("costs") val cost: List<Cost> = emptyList(),
)
