package tech.thatgravyboat.skyblockapi.api.environmental

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor

@Module
// TODO: confirm when this is on main
object WeatherAPI {

    private val dayOfYear: Int?
        get() {
            val season = DateTimeAPI.season ?: return null
            val day = DateTimeAPI.day
            if (day <= 0) return null
            return season.ordinal * 31 + (day - 1)
        }

    val isActive: Boolean
        get() = this.currentIntensity != null

    val currentIntensity: WeatherIntensity?
        get() {
            val day = dayOfYear ?: return null
            if (day % 3 != 2) return null

            val weatherEventIndex = day / 3
            return if (weatherEventIndex % 3 == 0) {
                WeatherIntensity.EXTREME
            } else {
                WeatherIntensity.MILD
            }
        }

    val currentEvent: WeatherEvent?
        get() {
            val intensity = currentIntensity ?: return null
            val group = WeatherGroup.getCurrentGroup() ?: return null
            return if (intensity == WeatherIntensity.EXTREME) group.extreme else group.mild
        }

    @Subscription
    private fun onCommand(event: RegisterSkyblockApiCommandsEvent) {
        event.registerWithCallback("weather") {
            Text.multiline(
                buildList {
                    add(
                        Text.of("Weather Active: ") {
                            if (isActive) {
                                append("Yes", TextColor.GREEN)
                            } else {
                                append("No", TextColor.RED)
                            }
                        },
                    )

                    if (isActive) {
                        val intensity = currentIntensity ?: return@buildList
                        add(
                            Text.of("Current Intensity: ") {
                                append(intensity.component)
                            },
                        )

                        val weatherEvent = currentEvent ?: return@buildList
                        add(
                            Text.of("Current Weather: ") {
                                append(weatherEvent.type.component)
                            },
                        )
                    }
                },
            ).sendWithPrefix()
        }
    }
}
