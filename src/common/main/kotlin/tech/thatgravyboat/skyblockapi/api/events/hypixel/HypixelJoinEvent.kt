package tech.thatgravyboat.skyblockapi.api.events.hypixel

import net.hypixel.data.region.Environment
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class HypixelJoinEvent(val environment: Environment) : SkyBlockEvent() {
    val onProduction: Boolean get() = environment == Environment.PRODUCTION
}
