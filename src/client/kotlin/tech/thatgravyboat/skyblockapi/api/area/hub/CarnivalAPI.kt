package tech.thatgravyboat.skyblockapi.api.area.hub

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

@Module
object CarnivalAPI {

    private val carnivalTokensRegex = Regexes.create(
        "scoreboard.carnival.tokens",
        "Carnival Tokens: (?<tokens>[\\d,kmb]+)"
    )

    var tokens: Int? = null
        private set

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        carnivalTokensRegex.anyMatch(event.added, "tokens") { (tokens) ->
            CarnivalAPI.tokens = tokens.parseFormattedInt()
        }
    }
}
