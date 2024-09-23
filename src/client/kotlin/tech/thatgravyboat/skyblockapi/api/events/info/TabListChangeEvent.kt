package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

/**
 * Fired when the tab list changes in SkyBlock.
 * This will only contain the info in the tab list and not the players.
 */
data class TabListChangeEvent(val old: List<List<String>>, val new: List<List<String>>) : SkyblockEvent()
