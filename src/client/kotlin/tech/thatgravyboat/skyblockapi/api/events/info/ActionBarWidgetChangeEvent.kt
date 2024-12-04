package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.data.item.ArmorStack
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import kotlin.time.Duration

open class RenderActionBarWidgetEvent(val widget: ActionBarWidget) : CancellableSkyBlockEvent()

open class ActionBarWidgetChangeEvent(
    val widget: ActionBarWidget,
    val old: String,
    val new: String,
) : SkyBlockEvent()

class HealthActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.HEALTH, old, new)

class DefenseActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DEFENSE, old, new)

class ManaActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.MANA, old, new)

class OverflowManaActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.OVERFLOW_MANA, old, new)

class RiftTimeActionBarWidgetChangeEvent(val time: Duration?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.RIFT_TIME, old, new)

class ArmadilloActionBarWidgetChangeEvent(val current: Float, val max: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMADILLO, old, new)

class ArmorStackActionBarWidgetChangeEvent(val current: Int, val type: ArmorStack?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMOR_STACK, old, new)

class SecretsActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SECRETS, old, new)

enum class ActionBarWidget {
    HEALTH,
    DEFENSE,
    MANA,
    NO_MANA,
    OVERFLOW_MANA,
    ABILITY,
    LOCATION,
    SKILL_XP,
    SKYBLOCK_XP,
    RIFT_TIME,
    ARMADILLO,
    CHARGES,
    ARMOR_STACK,
    CELLS_ALIGNMENT,
    SECRETS,
    ;

    private val string = toFormattedName()

    override fun toString() = string
}
