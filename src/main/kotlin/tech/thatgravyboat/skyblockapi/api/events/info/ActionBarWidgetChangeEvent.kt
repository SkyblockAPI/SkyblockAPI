package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.data.item.ArmorStack
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI.Skill
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import kotlin.time.Duration

public open class RenderActionBarWidgetEvent(public val widget: ActionBarWidget) : CancellableSkyBlockEvent()

public open class ActionBarWidgetChangeEvent(
    public val widget: ActionBarWidget,
    public val old: String,
    public val new: String,
) : SkyBlockEvent()

public class HealthActionBarWidgetChangeEvent(public val current: Int, public val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.HEALTH, old, new)

public class DefenseActionBarWidgetChangeEvent(public val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DEFENSE, old, new)

public class ManaActionBarWidgetChangeEvent(public val current: Int, public val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.MANA, old, new)

public class OverflowManaActionBarWidgetChangeEvent(public val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.OVERFLOW_MANA, old, new)

public class RiftTimeActionBarWidgetChangeEvent(public val time: Duration?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.RIFT_TIME, old, new)

public class ArmadilloActionBarWidgetChangeEvent(public val current: Float, public val max: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMADILLO, old, new)

public class ArmorStackActionBarWidgetChangeEvent(public val current: Int, public val type: ArmorStack?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMOR_STACK, old, new)

public class SecretsActionBarWidgetChangeEvent(public val current: Int, public val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SECRETS, old, new)

public class DrillActionBarWidgetChangeEvent(public val current: Int, public val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DRILL_FUEL, old, new)

public class PressureActionBarWidgetChangeEvent(public val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.PRESSURE, old, new)

public class SkillXpPercentActionBarWidgetChangeEvent(public val amount: Float, public val skill: Skill?, public val percent: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SKILL_XP, old, new)

public class SkillXpLiteralActionBarWidgetChangeEvent(public val amount: Float, public val skill: Skill?, public val current: Long, public val needed: Long, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SKILL_XP_LITERAL, old, new)

public class VitalityActionBarWidgetChangeEvent(public val current: Int, public val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.VITALITY, old, new)

public enum class ActionBarWidget {
    HEALTH,
    DEFENSE,
    MANA,
    NO_MANA,
    OVERFLOW_MANA,
    DRILL_FUEL,
    ABILITY,
    LOCATION,
    SKILL_XP,
    SKILL_XP_LITERAL,
    SKYBLOCK_XP,
    RIFT_TIME,
    ARMADILLO,
    CHARGES,
    ARMOR_STACK,
    CELLS_ALIGNMENT,
    SECRETS,
    PRESSURE,
    VITALITY,
    ;

    private val string = toFormattedName()

    override fun toString(): String = string
}
