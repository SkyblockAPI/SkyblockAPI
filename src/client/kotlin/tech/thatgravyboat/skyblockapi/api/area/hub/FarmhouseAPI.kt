package tech.thatgravyboat.skyblockapi.api.area.hub

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object FarmhouseAPI {

    private val scoreboardGroup = RegexGroup.SCOREBOARD.group("farmhouse")

    private val goldMedalsRegex = scoreboardGroup.create("gold", "GOLD medals: (?i)(?<gold>[\\d,kmb]+)")
    private val silverMedalsRegex = scoreboardGroup.create("silver", "SILVER medals: (?i)(?<silver>[\\d,kmb]+)")
    private val bronzeMedalsRegex = scoreboardGroup.create("bronze", "BRONZE medals: (?i)(?<bronze>[\\d,kmb]+)")

    var goldMedals: Int = 0
        private set

    var silverMedals: Int = 0
        private set

    var bronzeMedals: Int = 0
        private set

    @Subscription
    @OnlyIn(SkyBlockIsland.HUB, SkyBlockIsland.GARDEN)
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        goldMedalsRegex.anyMatch(event.added, "gold") { (gold) -> goldMedals = gold.toIntValue() }
        silverMedalsRegex.anyMatch(event.added, "silver") { (silver) -> silverMedals = silver.toIntValue() }
        bronzeMedalsRegex.anyMatch(event.added, "bronze") { (bronze) -> bronzeMedals = bronze.toIntValue() }
    }

    private fun reset() {
        goldMedals = 0
        silverMedals = 0
        bronzeMedals = 0
    }

    @Subscription
    fun onServerChange(event: ServerChangeEvent) {
        reset()
    }
}
