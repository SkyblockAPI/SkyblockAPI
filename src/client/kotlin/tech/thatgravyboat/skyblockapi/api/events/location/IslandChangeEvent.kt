package tech.thatgravyboat.skyblockapi.api.events.location

import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyblockIsland

data class IslandChangeEvent(val old: SkyblockIsland?, val new: SkyblockIsland?) : SkyblockEvent()
