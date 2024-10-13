package tech.thatgravyboat.skyblockapi.api.area.rift

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.RiftTimeActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.api.profile.CurrencyAPI
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyFound
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.component.anyMatch
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import kotlin.time.Duration

@Module
object RiftAPI {

    private val regexGroup = RegexGroup.TABLIST_WIDGET.group("good_to_know")

    private val visitedRiftRegex = regexGroup.create(
        "visited_rift",
        "^\\s*Visited Rift: (?<visited>\\d+) times"
    )
    private val lifetimeMotesRegex = regexGroup.create(
        "lifetime_motes",
        "^\\s*Lifetime Motes: (?<motes>[\\d,kmb]+)"
    )
    private val timecharmsRegex = regexGroup.create(
        "timecharms",
        "^\\s*Timecharms: (?<current>\\d+)/(?<max>\\d+)"
    )
    private val enigmaSoulsRegex = regexGroup.create(
        "enigma_souls",
        "^\\s*Enigma Souls: (?<current>\\d+)/(?<max>\\d+)"
    )
    private val monetezumaRegex = regexGroup.create(
        "monetezuma",
        "^\\s*Monetezuma: (?<current>\\d+)/(?<max>\\d+)"
    )
    private val effigiesRegex = ComponentRegex(regexGroup.create(
        "effigies",
        "^Effigies: (?<e1>⧯)(?<e2>⧯)(?<e3>⧯)(?<e4>⧯)(?<e5>⧯)(?<e6>⧯)"
    ))

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

    val effieges: List<Effigy> = listOf(
        Effigy(150, 73, 95),
        Effigy(193, 87, 119),
        Effigy(235, 104, 147),
        Effigy(293, 90, 134),
        Effigy(262, 93, 94),
        Effigy(240, 123, 118),
    )

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_RIFT)
    fun onActionBarWidgetChange(event: RiftTimeActionBarWidgetChangeEvent) {
        time = event.time
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_RIFT)
    fun onScoreboardChange(event: ScoreboardUpdateEvent) {
        effigiesRegex.anyMatch(event.components, "e1", "e2", "e3", "e4", "e5", "e6") { (one, two, three, four, five, six) ->
            effieges[0].enabled = one.style.color?.value == TextColor.RED
            effieges[1].enabled = two.style.color?.value == TextColor.RED
            effieges[2].enabled = three.style.color?.value == TextColor.RED
            effieges[3].enabled = four.style.color?.value == TextColor.RED
            effieges[4].enabled = five.style.color?.value == TextColor.RED
            effieges[5].enabled = six.style.color?.value == TextColor.RED
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_RIFT)
    @OnlyWidget(TabWidget.GOOD_TO_KNOW)
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        visitedRiftRegex.anyFound(event.new, "visited") { (visited) ->
            timesVisted = visited.toIntValue()
        }

        lifetimeMotesRegex.anyFound(event.new, "motes") { (motes) ->
            lifetimeMotes = motes.parseFormattedLong()
        }

        timecharmsRegex.anyFound(event.new, "current", "max") { (current, max) ->
            timecharms = Pair(current.toIntValue(), max.toIntValue())
        }

        enigmaSoulsRegex.anyFound(event.new, "current", "max") { (current, max) ->
            enigmaSouls = Pair(current.toIntValue(), max.toIntValue())
        }

        monetezumaRegex.anyFound(event.new, "current", "max") { (current, max) ->
            monetezuma = Pair(current.toIntValue(), max.toIntValue())
        }
    }

}
