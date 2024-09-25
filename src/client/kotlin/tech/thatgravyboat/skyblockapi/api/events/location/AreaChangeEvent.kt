package tech.thatgravyboat.skyblockapi.api.events.location

import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyblockArea

data class AreaChangeEvent(val old: SkyblockArea, val new: SkyblockArea) : SkyblockEvent()
