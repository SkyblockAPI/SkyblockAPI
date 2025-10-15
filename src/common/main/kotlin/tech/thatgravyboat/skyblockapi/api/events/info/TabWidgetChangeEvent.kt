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
    val isEmpty: Boolean get() = new.isEmpty()
    override fun post(bus: EventBus): Boolean = bus.post(this, this.widget)
}

@Suppress("unused")
enum class TabWidget(@Language("RegExp") regex: String) {
    // General
    AREA("(?:Area|Dungeon): (?<area>.*)"),
    PROFILE("Profile: (?<profile>.*)"),
    PET("Pet:"),
    DAILY_QUESTS("Daily Quests:"),
    SKILLS("Skills:(?: (?<avg>[\\d.]+)| (?<skill>.+) (?<level>\\S+): (?<progress>[\\d,.kMB%]+))?"),
    ELECTION("Election: (?<election>.*)"),
    BESTIARY("Bestiary:"),
    COLLECTION("Collection:"),
    STATS("Stats:"),
    EVENT("Event: (?<event>.*)"),
    EVENT_TRACKERS("Event Trackers:"),
    ACTIVE_EFFECTS("Active Effects:(?: \\((?<amount>\\d+)\\))?"),
    TIMERS("Timers:"),
    FIRE_SALE("Fire Sales: \\((?<amount>[\\d,.]+)\\)"),
    MINIONS("Minions: (?<amount>.*)"),

    // Mining
    FORGES("Forges:(?: \\((?<active>[\\d,.]+)/(?<max>[\\d,.]+)\\))?"),
    COMMISSIONS("Commissions:"),
    POWDERS("Powders:"),
    CRYSTALS("Crystals:"),
    MINING_EVENT("Mining Event: (?<event>.*)"),
    FROZEN_CORPSES("Frozen Corpses:"),
    PITY("Pity:"),
    PICKAXE_ABILITY("Pickaxe Ability:"),

    // Foraging
    STARBORN_TEMPLE("Starborn Temple:"),
    FOREST_WHISPERS("Forest Whispers: (?<amount>[\\dkmb,.]+)"),
    MOONGLADE_BEACON("Moonglade Beacon: (?<amount>[\\d,.]+) Stacks?"),

    // Garden + Farming
    COMPOSTER("Composter:"),
    JACOBS_CONTEST("Jacob's Contest:(?: (?<time>.*))?"),
    PESTS("Pests:(?: (?<amount>\\d+))?"),
    PEST_TRAPS("Pest Traps: (?<amount>[\\d,.]+)/(?<max>[\\d,.]+)"),
    VISITORS("Visitors: \\((?<amount>\\d+)\\)"),
    TRAPPER("Trapper:"),
    CROP_MILESTONES("Crop Milestones:"),

    // Crimson Isle
    REPUTATION("(?:Mage|Barbarian) Reputation:"),
    TROPHY_FISH("Trophy Fish:"),
    FACTION_QUESTS("Faction Quests:"),

    // Dungeons + Dungeon Hub
    DOWNED("Downed: (?<status>.*)"),
    TEAM_DEATHS("Team Deaths: (?<amount>\\d+)"),
    DISCOVERIES("Discoveries: (?<amount>\\d+)"),
    PUZZLES("Puzzles: \\((?<amount>\\d+)\\)"),
    RNG_METER("RNG Meter"),
    PARTY("Party: (?<party>.*)"),
    DUNGEONS("Dungeons:"),
    ESSENCE("Essence:"),

    // Rift
    GOOD_TO_KNOW("Good to know:"),
    SHEN("Shen: \\((?<duration>[\\ddmsh,]+)\\)"),
    ADVERTISEMENT("Advertisement:"),
    ;

    val regex = RegexGroup.TABLIST_WIDGET.create(name.lowercase(), regex)

    val currentLines: List<String>
        get() = TabListEventHandler.widgets[this].orEmpty()

    val isActive: Boolean
        get() = this in TabListEventHandler.widgets

    private val string = toFormattedName()

    override fun toString() = string
}
