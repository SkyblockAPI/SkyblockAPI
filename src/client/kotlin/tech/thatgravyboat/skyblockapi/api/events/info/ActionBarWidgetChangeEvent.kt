package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.data.item.ArmorStack
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyblockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent
import kotlin.time.Duration

open class RenderActionBarWidgetEvent(val widget: ActionBarWidget) : CancellableSkyblockEvent()

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

class RiftTimeActionBarWidgetChangeEvent(val time: Duration?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.RIFT_TIME, old, new)

class ArmadilloActionBarWidgetChangeEvent(val current: Float, val max: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMADILLO, old, new)

class ArmorStackActionBarWidgetChangeEvent(val current: Int, val type: ArmorStack?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMOR_STACK, old, new)

enum class ActionBarWidget {
    HEALTH,
    DEFENSE,
    MANA,
    NO_MANA,
    ABILITY,
    LOCATION,
    SKILL_XP,
    SKYBLOCK_XP,
    RIFT_TIME,
    ARMADILLO,
    CHARGES,
    ARMOR_STACK,
    CELLS_ALIGNMENT,
}
