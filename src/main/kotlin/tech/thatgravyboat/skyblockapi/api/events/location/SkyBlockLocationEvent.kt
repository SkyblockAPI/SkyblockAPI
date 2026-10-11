package tech.thatgravyboat.skyblockapi.api.events.location

import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public sealed class SkyBlockLocationEvent : SkyBlockEvent() {
    public object Join : SkyBlockLocationEvent()
    public object Leave : SkyBlockLocationEvent()
}
