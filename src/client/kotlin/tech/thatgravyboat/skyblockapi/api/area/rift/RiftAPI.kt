package tech.thatgravyboat.skyblockapi.api.area.rift

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.RiftTimeActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.CurrencyAPI
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes
import kotlin.time.Duration

@Module
object RiftAPI {

    private val visitedRiftRegex = Regexes.create(
        "tablist.widget.good_to_know.visited_rift",
        " Visited Rift: (?<visited>\\d+) times"
    )
    private val lifetimeMotesRegex = Regexes.create(
        "tablist.widget.good_to_know.lifetime_motes",
        " Lifetime Motes: (?<motes>[\\d,kmb]+)"
    )
    private val timecharmsRegex = Regexes.create(
        "tablist.widget.good_to_know.timecharms",
        " Timecharms: (?<current>\\d+)/(?<max>\\d+)"
    )
    private val enigmaSoulsRegex = Regexes.create(
        "tablist.widget.good_to_know.enigma_souls",
        " Enigma Souls: (?<current>\\d+)/(?<max>\\d+)"
    )
    private val monetezumaRegex = Regexes.create(
        "tablist.widget.good_to_know.monetezuma",
        " Monetezuma: (?<current>\\d+)/(?<max>\\d+)"
    )

    var time: Duration? = null
        private set

    val motes: Long
        get() = CurrencyAPI.motes

    var timesVisted: Int = 0
        private set

    var lifetimeMotes: Long = 0
        private set

    var timecharms: Pair<Int, Int> = Pair(0, 0)
        private set

    var enigmaSouls: Pair<Int, Int> = Pair(0, 0)
        private set

    var monetezuma: Pair<Int, Int> = Pair(0, 0)
        private set

    @Subscription
    fun onActionBarWidgetChange(event: RiftTimeActionBarWidgetChangeEvent) {
        time = event.time
    }

    @Subscription
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        if (event.widget != TabWidget.GOOD_TO_KNOW) return
        visitedRiftRegex.anyMatch(event.new, "visited") { (visited) ->
            timesVisted = visited.toIntValue()
        }

        lifetimeMotesRegex.anyMatch(event.new, "motes") { (motes) ->
            lifetimeMotes = motes.parseFormattedLong()
        }

        timecharmsRegex.anyMatch(event.new, "current", "max") { (current, max) ->
            timecharms = Pair(current.toIntValue(), max.toIntValue())
        }

        enigmaSoulsRegex.anyMatch(event.new, "current", "max") { (current, max) ->
            enigmaSouls = Pair(current.toIntValue(), max.toIntValue())
        }

        monetezumaRegex.anyMatch(event.new, "current", "max") { (current, max) ->
            monetezuma = Pair(current.toIntValue(), max.toIntValue())
        }
    }

}
