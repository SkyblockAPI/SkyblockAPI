package tech.thatgravyboat.skyblockapi.api.profile

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.DefenseActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.HealthActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ManaActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyblockIsland
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object StatsAPI {

    var health: Int = 0
        private set

    var maxHealth: Int = 100
        private set

    var defense: Int = 0
        private set

    var mana: Int = 0
        private set

    var maxMana: Int = 100
        private set

    @Subscription
    fun onActionBarWidget(event: ActionBarWidgetChangeEvent) {
        when (event) {
            is HealthActionBarWidgetChangeEvent -> {
                health = event.current
                maxHealth = event.max
            }
            is DefenseActionBarWidgetChangeEvent -> {
                defense = event.current
            }
            is ManaActionBarWidgetChangeEvent -> {
                mana = event.current
                maxMana = event.max
            }
        }

        if (LocationAPI.island == SkyblockIsland.THE_RIFT) {
            health = McPlayer.health
            maxHealth = McPlayer.maxHealth
        }
    }
}
