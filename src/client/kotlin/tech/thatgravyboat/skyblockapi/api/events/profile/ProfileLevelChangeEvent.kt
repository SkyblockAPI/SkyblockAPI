package tech.thatgravyboat.skyblockapi.api.events.profile

import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

data class ProfileLevelChangeEvent(val level: Int, val xp: Int, val newLevelXp: Int) : SkyblockEvent()
