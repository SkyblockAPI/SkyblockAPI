package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

data class ScoreboardTitleUpdateEvent(val old: String?, val new: String) : SkyblockEvent()
