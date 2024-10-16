package tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyFound
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findAll

@Module
object MineshaftAPI {

    private val widgetGroup = RegexGroup.TABLIST_WIDGET.group("mineshaft")
    private val scoreboardGroup = RegexGroup.SCOREBOARD.group("mineshaft")

    private val scrapRegex = widgetGroup.create(
        "scrap",
        "^\\s*Scrap: (?<scrap>\\d+/\\d)$"
    )
    private val corpseRegex = widgetGroup.create(
        "corpse",
        "^\\s*(?<corpse>\\w+): (?<state>.+)$"
    )
    private val mineshaftTypeRegex = scoreboardGroup.create(
        "type",
        "^\\d+/\\d+/\\d+ \\S+ (?<type>\\w{4})(?<number>\\d)$"
    )

    var mineshaftType: MineshaftType? = null
        private set

    var isCrystal: Boolean = false
        private set

    var scrap: Int = 0
        private set

    var corpses: List<Corpse> = listOf()
        private set

    @Subscription
    @OnlyIn(SkyBlockIsland.MINESHAFT)
    fun onWidgetUpdate(event: TabWidgetChangeEvent) {
        when (event.widget) {
            TabWidget.AREA -> {
                scrapRegex.anyFound(event.new, "scrap") { (scrap) ->
                    MineshaftAPI.scrap = scrap.toIntValue()
                }
            }
            TabWidget.FROZEN_CORPSES -> {
                corpses = buildList {
                    corpseRegex.findAll(event.new, "corpse", "state") { (corpse, state) ->
                        val type = CorpseType.byName(corpse) ?: return@findAll
                        val looted = state == "LOOTED"
                        add(Corpse(type, looted))
                    }
                }
            }
            else -> return
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.MINESHAFT)
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        mineshaftTypeRegex.anyFound(event.added, "type", "number") { (type, number) ->
            this.mineshaftType = MineshaftType.fromId(type)
            this.isCrystal = number == "2"
        }
    }

    private fun reset() {
        scrap = 0
        corpses = listOf()
        mineshaftType = null
        isCrystal = false
    }

    @Subscription
    fun onWorldChange(event: ServerChangeEvent) {
        reset()
    }
}
