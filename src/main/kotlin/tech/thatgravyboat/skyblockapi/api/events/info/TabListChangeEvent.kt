package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

typealias TabListUpdateEvent = TabListChangeEvent

/**
 * Fired when the tab list changes in SkyBlock.
 * This will only contain the info in the tab list and not the players.
 */
// TODO: change this to have new and old in both string and component form
data class TabListChangeEvent(
    val old: List<List<String>>,
    val new: List<List<Component>>,
) : SkyBlockEvent()
