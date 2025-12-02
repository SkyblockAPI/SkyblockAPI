package tech.thatgravyboat.skyblockapi.api.events.location

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland

data class IslandChangeEvent(val old: SkyBlockIsland?, val new: SkyBlockIsland?) : SkyBlockEvent()
