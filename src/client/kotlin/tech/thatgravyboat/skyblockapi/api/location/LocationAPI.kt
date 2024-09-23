package tech.thatgravyboat.skyblockapi.api.location

import net.hypixel.data.type.GameType
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object LocationAPI {

    var isOnSkyblock: Boolean = false
        private set

    var island: SkyblockIsland? = null
        private set

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        isOnSkyblock = event.type == GameType.SKYBLOCK
        island = if (isOnSkyblock && event.mode != null) {
            SkyblockIsland.getById(event.mode)
        } else {
            null
        }
        IslandChangeEvent(island).post(SkyBlockAPI.eventBus)
    }

    @Subscription
    fun onServerDisconnect(event: ServerDisconnectEvent) {
        isOnSkyblock = false
        island = null
    }

}
