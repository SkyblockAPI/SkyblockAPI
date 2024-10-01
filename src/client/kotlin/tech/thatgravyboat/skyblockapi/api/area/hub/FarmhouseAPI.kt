package tech.thatgravyboat.skyblockapi.api.area.hub

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

@Module
object FarmhouseAPI {

    private val goldMedalsRegex = Regexes.create("scoreboard.farmhouse.gold", "GOLD medals: (?<gold>\\d+)")
    private val silverMedalsRegex = Regexes.create("scoreboard.farmhouse.silver", "SILVER medals: (?<silver>\\d+)")
    private val bronzeMedalsRegex = Regexes.create("scoreboard.farmhouse.bronze", "BRONZE medals: (?<bronze>\\d+)")

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
