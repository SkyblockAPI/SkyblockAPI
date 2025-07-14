package tech.thatgravyboat.skyblockapi.api.events.location

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea

data class AreaChangeEvent(val old: SkyBlockArea, val new: SkyBlockArea) : SkyBlockEvent()
