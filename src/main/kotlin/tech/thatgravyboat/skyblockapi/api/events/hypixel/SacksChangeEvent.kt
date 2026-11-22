package tech.thatgravyboat.skyblockapi.api.events.hypixel

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

//~ if >= 26.4 'constructor' -> 'internal constructor'
public data class SacksChangeEvent constructor(
    val changedItems: List<ChangedSackItem>,
) : SkyBlockEvent()

//~ if >= 26.4 'constructor' -> 'internal constructor'
public data class ChangedSackItem constructor(
    val item: String,
    val diff: Int,
    @IntroducedAt("4.3.2")
    val id: SkyBlockId = SkyBlockId.item(item)
)
