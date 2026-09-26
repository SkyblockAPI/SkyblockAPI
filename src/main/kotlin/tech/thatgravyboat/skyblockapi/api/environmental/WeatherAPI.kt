package tech.thatgravyboat.skyblockapi.api.environmental

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
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

    private val nextWeatherDayOfYear: Int?
        get() = dayOfYear?.let { currentDay ->
            val remainder = currentDay % 3
            currentDay + if (remainder == 2) 3 else 2 - remainder
        }

    private fun getIntensityForDay(day: Int): WeatherIntensity {
        return if ((day / 3) % 3 == 0) WeatherIntensity.EXTREME else WeatherIntensity.MILD
    }

    val isActive: Boolean
        get() = this.currentIntensity != null

    val currentIntensity: WeatherIntensity?
        get() = dayOfYear?.takeIf { it % 3 == 2 }?.let(::getIntensityForDay)

    val currentEvent: WeatherEvent?
        get() {
            val intensity = currentIntensity ?: return null
            val group = WeatherGroup.getCurrentGroup() ?: return null
            return if (intensity == WeatherIntensity.EXTREME) group.extreme else group.mild
        }

    val nextIntensity: WeatherIntensity?
        get() = nextWeatherDayOfYear?.let(::getIntensityForDay)

    val nextWeatherAt: SkyBlockInstant?
        get() {
            val currentDay = dayOfYear ?: return null
            val nextDay = nextWeatherDayOfYear ?: return null
            val season = DateTimeAPI.season ?: return null
            val day = DateTimeAPI.day
            if (day <= 0) return null

            val startOfDay = SkyBlockInstant(
                year = SkyBlockInstant.now().year,
                month = season.ordinal + 1,
                day = day,
            )

            return startOfDay + (nextDay - currentDay).skyblockDays
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

                    val nextIntensity = nextIntensity
                    val nextWeatherInstant = nextWeatherAt
                    if (nextIntensity != null && nextWeatherInstant != null) {
                        add(
                            Text.of("Next Intensity: ") {
                                append(nextIntensity.component)
                            },
                        )

                        val durationUntil = nextWeatherInstant.instant - currentInstant()
                        val minutes = durationUntil.inWholeMinutes
                        val seconds = durationUntil.inWholeSeconds % 60

                        add(
                            Text.of("Next Weather In: ") {
                                append("${minutes}m ${seconds}s", TextColor.YELLOW)
                            },
                        )
                    }
                },
            ).sendWithPrefix()
        }
    }
}
