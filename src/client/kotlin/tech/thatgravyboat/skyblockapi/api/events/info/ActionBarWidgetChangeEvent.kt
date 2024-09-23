package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

data class ActionBarWidgetChangeEvent(
    val widget: ActionBarWidget,
    val old: String,
    val new: String,
) : SkyblockEvent()

enum class ActionBarWidget {
    HEALTH,
    DEFENSE,
    MANA,
    ABILITY,
    LOCATION,
    SKILL_XP,
    SKYBLOCK_XP,
}
