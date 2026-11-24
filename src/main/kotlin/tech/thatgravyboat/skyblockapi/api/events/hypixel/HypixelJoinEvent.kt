package tech.thatgravyboat.skyblockapi.api.events.hypixel

import net.hypixel.data.region.Environment
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public data class HypixelJoinEvent(val environment: Environment) : SkyBlockEvent() {
    val onAlpha: Boolean get() = environment == Environment.BETA
}
