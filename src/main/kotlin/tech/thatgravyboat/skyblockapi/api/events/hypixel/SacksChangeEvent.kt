package tech.thatgravyboat.skyblockapi.api.events.hypixel

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId

//~ if >= 26.4 'constructor' -> 'internal constructor'
public data class SacksChangeEvent constructor(
    val changedItems: List<ChangedSackItem>,
) : SkyBlockEvent()

//~ if >= 26.4 'constructor' -> 'internal constructor'
public data class ChangedSackItem constructor(
    //? if < 26.4
    val item: String,
    val diff: Int,
    //? if < 26.4
    @IntroducedAt("4.3.2")
    val id: SkyBlockId
    //? if < 26.4
    = SkyBlockId.item(item)
)
