package tech.thatgravyboat.skyblockapi.api.events.profile

import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

/**
 * An event posted when the player's profile changes.
 */
data class ProfileChangeEvent(val name: String) : SkyblockEvent()
