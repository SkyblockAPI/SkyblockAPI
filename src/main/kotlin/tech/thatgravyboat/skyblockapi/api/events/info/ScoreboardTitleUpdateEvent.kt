package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public typealias ScoreboardTitleChangeEvent = ScoreboardTitleUpdateEvent

public data class ScoreboardTitleUpdateEvent(val old: String?, val new: String) : SkyBlockEvent()
