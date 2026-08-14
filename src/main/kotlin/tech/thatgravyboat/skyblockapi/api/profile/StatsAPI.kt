package tech.thatgravyboat.skyblockapi.api.profile

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.*
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

    var vitality: Int = 0
        private set

    var maxVitality: Int = 100
        private set

    @Deprecated("Use vitality instead", ReplaceWith("vitality"))
    val vitaliy: Int get() = vitality
    @Deprecated("Use maxVitality instead", ReplaceWith("maxVitality"))
    val maxVitaliy: Int get() = maxVitality

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
                vitality = event.current
                maxVitality = event.max
            }
        }

        if (LocationAPI.island == SkyBlockIsland.THE_RIFT) {
            health = McPlayer.health
            maxHealth = McPlayer.maxHealth
        }
    }
}
