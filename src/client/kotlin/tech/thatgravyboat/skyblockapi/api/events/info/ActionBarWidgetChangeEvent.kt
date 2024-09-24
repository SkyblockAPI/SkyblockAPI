package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

open class ActionBarWidgetChangeEvent(
    val widget: ActionBarWidget,
    val old: String,
    val new: String,
) : SkyblockEvent()

class HealthActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.HEALTH, old, new)

class DefenseActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DEFENSE, old, new)

class ManaActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.MANA, old, new)

enum class ActionBarWidget {
    HEALTH,
    DEFENSE,
    MANA,
    ABILITY,
    LOCATION,
    SKILL_XP,
    SKYBLOCK_XP,
}
