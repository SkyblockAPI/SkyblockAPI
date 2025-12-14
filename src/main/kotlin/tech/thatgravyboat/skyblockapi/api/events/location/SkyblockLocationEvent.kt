package tech.thatgravyboat.skyblockapi.api.events.location

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

sealed class SkyblockLocationEvent : SkyBlockEvent() {
    object Join : SkyblockLocationEvent()
    object Leave : SkyblockLocationEvent()
}
