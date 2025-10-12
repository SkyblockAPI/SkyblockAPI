package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.impl.events.TabListEventHandler
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup

data class TabWidgetChangeEvent(
    val widget: TabWidget,
    val old: List<String>,
    val new: List<String>,
    val newComponents: List<Component>,
) : SkyBlockEvent() {

    override fun post(bus: EventBus): Boolean = bus.post(this, this.widget)
}

@Suppress("unused")
enum class TabWidget(@Language("RegExp") regex: String) {
    AREA("(?:Area|Dungeon): (?<area>.*)"),
    PROFILE("Profile: (?<profile>.*)"),
    PET("Pet:"),
    DAILY_QUESTS("Daily Quests:"),
    FORGES("Forges:(?: \\((?<active>[\\d,.]+)/(?<max>[\\d,.]+)\\))?"),
    COMMISSIONS("Commissions:"),
    SKILLS("Skills:(?: (?<avg>[\\d.]+))?"),
    POWDERS("Powders:"),
    ELECTION("Election: (?<election>.*)"),
    CRYSTALS("Crystals:"),
    BESTIARY("Bestiary:"),
    COLLECTION("Collection:"),
    STATS("Stats:"),
    EVENT("Event: (?<event>.*)"),
    PARTY("Party: (?<party>.*)"),
    MINIONS("Minions: (?<party>.*)"), // Party
    DUNGEONS("Dungeons:"),
    ESSENCE("Essence:"),
    GOOD_TO_KNOW("Good to know:"),
    SHEN("Shen: \\((?<duration>[\\ddmsh,]+)\\)"),
    ADVERTISEMENT("Advertisement:"),
    TRAPPER("Trapper:"),
    EVENT_TRACKERS("Event Trackers:"),
    FROZEN_CORPSES("Frozen Corpses:"),
    ACTIVE_EFFECTS("Active Effects:(?: \\((?<amount>\\d+)\\))?"),
    MINING_EVENT("Mining Event: (?<event>.*)"),
    TIMERS("Timers:"),
    COMPOSTER("Composter:"),
    JACOBS_CONTEST("Jacob's Contest:(?: (?<time>.*))?"),
    PESTS("Pests:(?: (?<amount>\\d+))?"),
    PEST_TRAPS("Pest Traps: (?<amount>[\\d,.]+)/(?<max>[\\d,.]+)"),
    VISITORS("Visitors: \\((?<amount>\\d+)\\)"),
    RNG_METER("RNG Meter"),
    DOWNED("Downed: (?<status>.*)"),
    TEAM_DEATHS("Team Deaths: (?<amount>\\d+)"),
    DISCOVERIES("Discoveries: (?<amount>\\d+)"),
    PUZZLES("Puzzles: \\((?<amount>\\d+)\\)"),
    REPUTATION("(?:Mage|Barbarian) Reputation:"),
    TROPHY_FISH("Trophy Fish:"),
    FACTION_QUESTS("Faction Quests:"),
    FOREST_WHISPERS("Forest Whispers: (?<amount>[\\dkmb,.]+)"),
    MOONGLADE_BEACON("Moonglade Beacon: (?<amount>[\\d,.]+) Stacks?"),
    FIRE_SALE("Fire Sales: \\((?<amount>[\\d,.]+)\\)"),
    ;

    val regex = RegexGroup.TABLIST_WIDGET.create(name.lowercase(), regex)

    val currentLines: List<String>
        get() = TabListEventHandler.widgets[this].orEmpty()

    val isActive: Boolean
        get() = this in TabListEventHandler.widgets

    private val string = toFormattedName()

    override fun toString() = string
}
