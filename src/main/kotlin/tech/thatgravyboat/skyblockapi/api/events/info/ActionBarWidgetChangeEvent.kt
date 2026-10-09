package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.api.data.item.ArmorStack
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI.Skill
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import kotlin.time.Duration

public open class RenderActionBarWidgetEvent(val widget: ActionBarWidget) : CancellableSkyBlockEvent()

public open class ActionBarWidgetChangeEvent(
    public val widget: ActionBarWidget,
    public val old: String,
    public val new: String,
) : SkyBlockEvent()

public class HealthActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.HEALTH, old, new)

public class DefenseActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DEFENSE, old, new)

public class ManaActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.MANA, old, new)

public class OverflowManaActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.OVERFLOW_MANA, old, new)

public class RiftTimeActionBarWidgetChangeEvent(val time: Duration?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.RIFT_TIME, old, new)

public class ArmadilloActionBarWidgetChangeEvent(val current: Float, val max: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMADILLO, old, new)

public class ArmorStackActionBarWidgetChangeEvent(val current: Int, val type: ArmorStack?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMOR_STACK, old, new)

public class SecretsActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SECRETS, old, new)

public class DrillActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DRILL_FUEL, old, new)

public class PressureActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.PRESSURE, old, new)

public class SkillXpPercentActionBarWidgetChangeEvent(val amount: Float, val skill: Skill?, val percent: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SKILL_XP, old, new)

public class SkillXpLiteralActionBarWidgetChangeEvent(val amount: Float, val skill: Skill?, val current: Long, val needed: Long, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SKILL_XP_LITERAL, old, new)

public class VitalityActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
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

    override fun toString() = string
}
