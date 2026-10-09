package tech.thatgravyboat.skyblockapi.api.events.location

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea

public data class AreaChangeEvent(val old: SkyBlockArea, val new: SkyBlockArea) : SkyBlockEvent()
