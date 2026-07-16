package tech.thatgravyboat.skyblockapi.api.profile.reputation

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.ReputationStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.DebugBuilder
import tech.thatgravyboat.skyblockapi.utils.ApiDebug
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object ReputationAPI {
    private val tabWidgetGroup = RegexGroup.TABLIST_WIDGET.group("reputation")

    private val reputationAmountRegex = tabWidgetGroup.create(
        "amount",
        " (?<amount>[\\d,]+)",
    )

    var currentFaction: Faction?
        get() = ReputationStorage.currentFaction
        private set(value) {
            ReputationStorage.updateFaction(value)
        }

    var currentReputation: Int
        get() = reputation[currentFaction] ?: 0
        private set(value) {
            ReputationStorage.updateReputation(currentFaction ?: return, value)
        }

    val reputation: Map<Faction, Int>
        get() = ReputationStorage.reputation


    @Subscription
    @OnlyWidget(TabWidget.REPUTATION)
    fun onTabWidget(event: TabWidgetChangeEvent) {
        val matchResult = event.matchResult ?: return
        val type = Faction.byNameOrNull(matchResult.groupValues[1]) ?: return

        currentFaction = type

        reputationAmountRegex.anyMatch(event.new, "amount") { (amount) ->
            currentReputation = amount.toIntValue()
        }
    }

    @ApiDebug("Reputation")
    internal fun debug(builder: DebugBuilder) = with(builder) {
        fields(
            ::currentFaction,
            ::reputation,
        )
    }

}
