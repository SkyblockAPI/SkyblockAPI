package tech.thatgravyboat.skyblockapi.api.events.location

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

sealed class SkyBlockLocationEvent : SkyBlockEvent() {
    object Join : SkyBlockLocationEvent()
    object Leave : SkyBlockLocationEvent()
}
