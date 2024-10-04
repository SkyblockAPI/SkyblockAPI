package tech.thatgravyboat.skyblockapi.api.area.hub

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object FarmhouseAPI {

    private val regexGroup = RegexGroup.SCOREBOARD.group("farmhouse")

    private val goldMedalsRegex = regexGroup.create("gold", "GOLD medals: (?<gold>\\d+)")
    private val silverMedalsRegex = regexGroup.create("silver", "SILVER medals: (?<silver>\\d+)")
    private val bronzeMedalsRegex = regexGroup.create("bronze", "BRONZE medals: (?<bronze>\\d+)")

    var goldMedals: Int? = null
        private set

    var silverMedals: Int? = null
        private set

    var bronzeMedals: Int? = null
        private set

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        goldMedalsRegex.anyMatch(event.added, "gold") { (gold) -> goldMedals = gold.toInt() }
        silverMedalsRegex.anyMatch(event.added, "silver") { (silver) -> silverMedals = silver.toInt() }
        bronzeMedalsRegex.anyMatch(event.added, "bronze") { (bronze) -> bronzeMedals = bronze.toInt() }
    }
}
