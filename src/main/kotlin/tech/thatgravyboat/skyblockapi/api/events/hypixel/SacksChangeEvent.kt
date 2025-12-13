package tech.thatgravyboat.skyblockapi.api.events.hypixel

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class SacksChangeEvent(
    val changedItems: List<ChangedSackItem>,
) : SkyBlockEvent()

data class ChangedSackItem(
    val item: String,
    val diff: Int,
)
