package tech.thatgravyboat.skyblockapi.impl.events.profile

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileLevelChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object ProfileLevelEventHandler {

    private val regexGroup = RegexGroup.TABLIST_WIDGET.group("profile")

    private val levelRegex = regexGroup.create(
        "level",
        " SB Level: \\[(?<level>\\d+)] (?<xp>\\d+)/(?<nextXp>\\d+) XP"
    )

    private var lastLevel: Int = 0
    private var lastXp: Int = 0

    @Subscription
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        if (event.widget != TabWidget.PROFILE) return
        val level = event.new.getOrNull(1) ?: return
        levelRegex.match(level, "level", "xp", "nextXp") { (level, xp, nextXp) ->
            val newLevel = level.toIntOrNull() ?: return@match
            val newXp = xp.toIntOrNull() ?: return@match
            val newXpToNextLevel = nextXp.toIntOrNull() ?: return@match
            if (newLevel != lastLevel || newXp != lastXp) {
                lastLevel = newLevel
                lastXp = newXp
                ProfileLevelChangeEvent(newLevel, newXp, newXpToNextLevel).post(SkyBlockAPI.eventBus)
            }
        }
    }
}
