package tech.thatgravyboat.skyblockapi.api.profile

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ArmadilloActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.DefenseActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.HealthActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ManaActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.OverflowManaActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.VitalityActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.LocationAPI
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McPlayer

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

    var overflowMana: Int = 0
        private set

    var vitaliy: Int = 0
        private set

    var maxVitaliy: Int = 100
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

            is OverflowManaActionBarWidgetChangeEvent -> {
                overflowMana = event.current
            }

            is ArmadilloActionBarWidgetChangeEvent -> {
                val healthPercent = McPlayer.health.toFloat() / McPlayer.maxHealth.toFloat()
                health = (maxHealth * healthPercent).toInt()
            }

            is VitalityActionBarWidgetChangeEvent -> {
                vitaliy = event.current
                maxVitaliy = event.max
            }
        }

        if (LocationAPI.island == SkyBlockIsland.THE_RIFT) {
            health = McPlayer.health
            maxHealth = McPlayer.maxHealth
        }
    }
}
