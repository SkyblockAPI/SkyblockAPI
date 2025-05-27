package tech.thatgravyboat.skyblockapi.api.area.hub

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.utils.extentions.parseColonDuration
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import kotlin.time.Duration

@Module
object CarnivalAPI {

    private val group = RegexGroup.SCOREBOARD.group("carnival")

    private val carnivalTokensRegex = group.create(
        "tokens",
        "Carnival Tokens: (?i)(?<tokens>[\\d,kmb]+)",
    )

    private val carnivalDurationRegex = group.create(
        "duration",
        "Carnival (?i)(?<duration>[\\d:]+)",
    )

    var tokens: Int = 0
        private set

    var duration: Duration = Duration.ZERO
        private set

    @Subscription
    @OnlyIn(SkyBlockIsland.HUB)
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        carnivalTokensRegex.anyMatch(event.added, "tokens") { (tokens) ->
            this.tokens = tokens.parseFormattedInt()
        }
        carnivalDurationRegex.anyMatch(event.added, "duration") { (duration) ->
            this.duration = duration.parseColonDuration() ?: Duration.ZERO
        }
    }

    private fun reset() {
        tokens = 0
        duration = Duration.ZERO
    }

    @Subscription
    fun onDisconnect(event: ServerDisconnectEvent) = reset()

    @Subscription
    fun onSwapProfile(event: ProfileChangeEvent) = reset()
}
