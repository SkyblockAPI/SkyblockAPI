package tech.thatgravyboat.skyblockapi.api.area.dungeon

import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabListChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.AreaChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.IslandChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerHotbarChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseDuration
import tech.thatgravyboat.skyblockapi.utils.extentions.parseRomanOrArabic
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.find
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import kotlin.time.Duration

@Module
object DungeonAPI {

    private val scoreboardGroup = RegexGroup.SCOREBOARD.group("dungeon")
    private val tablistGroup = RegexGroup.TABLIST.group("dungeon")
    private val chatGroup = RegexGroup.CHAT.group("dungeon")

    private val dungeonFloorRegex = scoreboardGroup.create(
        "floor",
        "The Catacombs \\((?<floor>.+)\\)",
    )
    private val timeRegex = scoreboardGroup.create(
        "time",
        "Time Elapsed: (?<time>[\\dhms ]+)",
    )
    private val roomIdRegex = scoreboardGroup.create(
        "room.id",
        "\\d+/\\d+/\\d+ .+? (?<id>.+)"
    )

    private val partyAmountRegex = tablistGroup.create(
        "party",
        "\\s*Party \\((?<amount>\\d+)\\)",
    )
    private val classRegex = tablistGroup.create(
        "player.class",
        "(?:\\[.+] ?)*(?<name>\\S+) .+\\((?<class>\\S+) (?<level>.+)\\)",
    )
    private val deadTeammateRegex = tablistGroup.create(
        "player.dead",
        "\\[.+] (?<name>\\S+) .+\\(DEAD\\)",
    )
    private val milestoneRegex = tablistGroup.create(
        "milestone",
        "\\s*Your Milestone: ☠(?<milestone>.)"
    )

    private val startRegex = chatGroup.create(
        "start",
        "\\[NPC] Mort: Here, I found this map when I first entered the dungeon\\.",
    )
    private val uniqueClassRegex = chatGroup.create(
        "class.unique",
        "Your .+ stats are doubled because you are the only player using this class!",
    )
    private val bossStartRegex = chatGroup.create(
        "boss.start",
        "^\\[BOSS] (?<boss>.+?):",
    )
    private val endRegex = chatGroup.create(
        "end",
        "\\s+(?:Master Mode|The) Catacombs - (?:Entrance|Floor [XVI]+)"
    )

    var ownPlayer: DungeonPlayer? = null
        private set

    val dungeonClass: DungeonClass? get() = ownPlayer?.dungeonClass

    val classLevel: Int get() = ownPlayer?.classLevel ?: 0

    var dungeonFloor: DungeonFloor? = null
        private set

    var uniqueClass: Boolean = false
        private set

    var started: Boolean = false
        private set

    var completed: Boolean = false
        private set

    var inBoss: Boolean = false
        private set

    var milestone: Int = 0
        private set

    var partySize: Int = 0
        private set

    var teammates: List<DungeonPlayer> = emptyList()
        private set

    var time: Duration = Duration.ZERO
        private set

    var roomId: String? = null
        private set

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_CATACOMBS)
    fun onAreaChange(event: AreaChangeEvent) {
        dungeonFloorRegex.find(event.new.name, "floor") { (floor) ->
            dungeonFloor = DungeonFloor.getByName(floor)
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_CATACOMBS)
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        for (line in event.added) {
            timeRegex.findThenNull(line, "time") { (time) ->
                this.time = time.parseDuration() ?: return@findThenNull
            } ?: continue
            roomIdRegex.findThenNull(line, "id") { (roomId) ->
                this.roomId = roomId
            } ?: continue
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_CATACOMBS)
    fun onChat(event: ChatReceivedEvent.Pre) {
        val message = event.text
        if (!started && startRegex.matches(message)) {
            started = true
            return
        }
        if (uniqueClassRegex.matches(message)) {
            uniqueClass = true
            return
        }
        if (!inBoss && dungeonFloor != DungeonFloor.E) {
            bossStartRegex.findThenNull(message, "boss") { (boss) ->
                if (boss != "The Watcher") return@findThenNull
                inBoss = dungeonFloor?.chatBossName == boss
            } ?: return
        }
        if (started && endRegex.matches(message)) {
            completed = true
            return
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_CATACOMBS)
    fun onTablistUpdate(event: TabListChangeEvent) {
        // first column
        val firstColumn = event.new.firstOrNull() ?: return
        val first = firstColumn.firstOrNull() ?: return
        partyAmountRegex.find(first.stripped, "amount") { (amount) ->
            this.partySize = amount.toIntValue()
        }

        val ownName = McPlayer.name

        for (line in firstColumn) {
            val stripped = line.stripped
            classRegex.findThenNull(stripped, "name", "class", "level") { (name, dungeonClass, level) ->
                val dungeonPlayer = teammates.find { it.name == name }
                if (dungeonPlayer != null) {
                    if (name != ownName) dungeonPlayer.dead = false
                    if (dungeonPlayer.missingData()) {
                        dungeonPlayer.dungeonClass = DungeonClass.getByName(dungeonClass)
                        dungeonPlayer.classLevel = level.parseRomanOrArabic()
                    }
                    return@findThenNull
                }
                val playerClass = DungeonClass.getByName(dungeonClass) ?: return@findThenNull
                val player = DungeonPlayer(name, playerClass, level.parseRomanOrArabic())
                if (name == ownName) {
                    ownPlayer = player
                }
                teammates += player
            } ?: continue
            deadTeammateRegex.find(stripped, "name") { (name) ->
                var dungeonPlayer = teammates.find { it.name == name }
                if (dungeonPlayer == null) {
                    dungeonPlayer = DungeonPlayer(name, null, null)
                    teammates += dungeonPlayer
                }
                dungeonPlayer.dead = true
            }
        }

        val secondColumn = event.new.getOrNull(1) ?: return
        for (line in secondColumn) {
            milestoneRegex.findThenNull(line.stripped, "milestone") { (milestone) ->
                this.milestone = milestoneCharToInt(milestone.first())
            } ?: break
        }
    }

    @Subscription
    @OnlyIn(SkyBlockIsland.THE_CATACOMBS)
    fun onPlayerHotbarUpdate(event: PlayerHotbarChangeEvent) {
        if (event.slot != 0) return
        val id = event.item.getData(DataTypes.ID)
        ownPlayer?.dead = id == "HAUNT_ABILITY"
    }

    private fun reset() {
        dungeonFloor = null
        ownPlayer = null
        uniqueClass = false
        started = false
        completed = false
        inBoss = false
        milestone = 0
        partySize = 0
        teammates = emptyList()
        time = Duration.ZERO
        roomId = null
    }

    @Subscription
    fun onIslandChange(event: IslandChangeEvent) {
        reset()
    }

    private fun milestoneCharToInt(char: Char): Int = if (char in '❶'..'❾') '❶'.code - char.code + 1 else 0

}
