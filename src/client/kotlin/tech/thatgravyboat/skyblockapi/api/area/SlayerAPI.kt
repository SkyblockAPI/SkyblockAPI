package tech.thatgravyboat.skyblockapi.api.area

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseRomanNumeral
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object SlayerAPI {

    private val slayerGroup = RegexGroup.SCOREBOARD.group("slayer")
    private val slayerQuestRegex = slayerGroup.create("quest", "Slayer Quest")
    private val slayerTypeRegex = slayerGroup.create("type", "(?<type>[\\w ]+) (?<level>[MDCLXVI]+)")
    private val slayerAmountRegex = slayerGroup.create(
        "amount",
        " \\(?(?<amount>[\\d,]+)/(?<total>[\\d,]+)\\)? (Combat XP|Kills)"
    )
    private val slayerBossTextRegex = slayerGroup.create(
        "boss",
        "(?<text>Slay the boss!|Boss slain!)"
    )

    var type: SlayerType? = null
        private set
    var level: Int = 0
        private set

    var text: String? = null
        private set
    var current: Int = 0
        private set
    var max: Int = 0
        private set

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        if (event.removed.any { slayerQuestRegex.matches(it) }) {
            reset()
        } else if (this.type == null && this.level == 0) {
            val index = event.added.indexOfFirst { slayerQuestRegex.matches(it) }
            if (index != -1 && event.new.size > index) {
                slayerTypeRegex.match(event.added[index + 1], "type", "level") { (type, level) ->
                    this.type = SlayerType.fromDisplayName(type)
                    this.level = level.parseRomanNumeral()
                }
            }
        } else if (event.added.isNotEmpty()) {
            slayerAmountRegex.anyMatch(event.added, "amount", "total") { (amount, total) ->
                this.current = amount.toIntValue()
                this.max = total.toIntValue()
            }
            slayerBossTextRegex.anyMatch(event.added, "text") { (text) ->
                this.text = text
            }
        }
    }

    private fun reset() {
        this.type = null
        this.text = null
        this.level = 0
        this.current = 0
        this.max = 0
    }
}

enum class SlayerType(val displayName: String) {
    REVENANT_HORROR("Revenant Horror"),
    TARANTULA_BROODFATHER("Tarantula Broodfather"),
    SVEN_PACKMASTER("Sven Packmaster"),
    VOIDGLOOM_SERAPH("Voidgloom Seraph"),
    RIFTSTALKER_BLOODFIEND("Riftstalker Bloodfiend"),
    INFERNO_DEMONLORD("Inferno Demonlord"),
    ;

    companion object {

        fun fromDisplayName(displayName: String): SlayerType? = entries.firstOrNull {
            it.displayName.equals(displayName, ignoreCase = true)
        }
    }
}
