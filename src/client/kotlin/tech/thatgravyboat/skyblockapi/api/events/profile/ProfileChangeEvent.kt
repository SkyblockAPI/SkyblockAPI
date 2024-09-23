package tech.thatgravyboat.skyblockapi.api.events.profile

import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent
import java.util.UUID

/**
 * An event posted when the player's profile changes.
 */
data class ProfileChangeEvent(val id: UUID, val name: String) : SkyblockEvent()
