package tech.thatgravyboat.skyblockapi.api.events.hypixel

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public data class SacksChangeEvent(
    val changedItems: List<ChangedSackItem>,
) : SkyBlockEvent()

public data class ChangedSackItem(
    val item: String,
    val diff: Int,
)
