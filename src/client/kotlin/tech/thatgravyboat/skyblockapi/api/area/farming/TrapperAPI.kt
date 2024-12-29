package tech.thatgravyboat.skyblockapi.api.area.farming

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockArea
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockAreas
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object TrapperAPI {

    private val peltsRegex = RegexGroup.SCOREBOARD.create("trapper.pelts", "Pelts: (?<pelts>[\\d,kmb]+)")
    private val peltsTabListRegex = RegexGroup.TABLIST_WIDGET.create("trapper.pelts", " Pelts: (?<pelts>[\\d,kmb]+)")
    private val animalRegex = RegexGroup.CHAT.create(
        "trapper.animals",
        "\\[NPC] Trevor: You can find your (?<type>\\w+) animal near the (?<location>.*).",
    )

    var pelts: Int = 0
        private set

    var trackedType: TrapperAnimalType = TrapperAnimalType.UNKNOWN
        private set

    var trackedLocation: SkyBlockArea = SkyBlockAreas.NONE
        private set

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_BARN)
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        peltsRegex.anyMatch(event.added, "pelts") { (pelts) ->
            this.pelts = pelts.parseFormattedInt()
        }
    }

    @Subscription
    @OnlyWidget(TabWidget.TRAPPER)
    @OnlyIn(SkyBlockIsland.THE_BARN)
    fun onTabListWidgetUpdate(event: TabWidgetChangeEvent) {
        peltsTabListRegex.anyMatch(event.new, "pelts") { (pelts) ->
            this.pelts = pelts.parseFormattedInt()
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_BARN)
    fun onChatMessage(event: ChatReceivedEvent.Pre) {
        animalRegex.match(event.text, "type", "location") { (type, location) ->
            trackedType = TrapperAnimalType.fromString(type)
            trackedLocation = SkyBlockArea(location)
        }
    }

    private fun reset() {
        this.pelts = 0
        this.trackedType = TrapperAnimalType.UNKNOWN
        this.trackedLocation = SkyBlockAreas.NONE
    }

    @Subscription
    fun onProfileChange(event: ProfileChangeEvent) = reset()

    @Subscription
    fun onDisconnect(event: ServerDisconnectEvent) = reset()
}

