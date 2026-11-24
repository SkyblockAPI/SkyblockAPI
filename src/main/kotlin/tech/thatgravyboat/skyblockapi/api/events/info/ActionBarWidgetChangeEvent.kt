package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI.Skill
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import kotlin.time.Duration
import tech.thatgravyboat.skyblockapi.api.data.item.ArmorStack as ArmorStackType

public class RenderActionBarWidgetEvent(public val widget: ActionBarWidget) : CancellableSkyBlockEvent()

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent"))
public open class ActionBarWidgetChangeEvent(
    public val widget: ActionBarWidget,
    public val old: String,
    public val new: String,
) : SkyBlockEvent()

private typealias ABWUE = ActionBarWidgetUpdateEvent

public open class ActionBarWidgetUpdateEvent private constructor(
    public val widget: ActionBarWidget,
    public val old: Component,
    public val new: Component,
) : SkyBlockEvent() {
    public class Health(public val current: Int, public val max: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.HEALTH, old, new)

    public class Defense(public val current: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.DEFENSE, old, new)

    public class Mana(public val current: Int, public val max: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.MANA, old, new)

    public class NoMana(old: Component, new: Component) : ABWUE(ActionBarWidget.NO_MANA, old, new)

    public class Vitality(public val current: Int, public val max: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.VITALITY, old, new)

    public class OverflowMana(public val current: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.OVERFLOW_MANA, old, new)

    public class RiftTime(public val time: Duration, old: Component, new: Component) : ABWUE(ActionBarWidget.RIFT_TIME, old, new)

    public class Armadillo(public val current: Float, public val max: Float, old: Component, new: Component) : ABWUE(ActionBarWidget.ARMADILLO, old, new)

    public class ArmorStack(public val current: Int, public val type: ArmorStackType, old: Component, new: Component) :
        ABWUE(ActionBarWidget.ARMOR_STACK, old, new)

    public class Secrets(public val current: Int, public val max: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.SECRETS, old, new)

    public class Drill(public val current: Int, public val max: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.DRILL_FUEL, old, new)

    public class Ability(public val manaAmount: Int, public val ability: Component, old: Component, new: Component) : ABWUE(ActionBarWidget.ABILITY, old, new)

    public class Pressure(public val current: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.PRESSURE, old, new)

    public class SkyBlockXp(public val gained: Long, old: Component, new: Component) : ABWUE(ActionBarWidget.SKYBLOCK_XP, old, new)

    public class Charges(public val current: Int, public val max: Component, old: Component, new: Component) : ABWUE(ActionBarWidget.CHARGES, old, new)

    public class Location(public val location: Component, old: Component, new: Component) : ABWUE(ActionBarWidget.LOCATION, old, new)

    public class CellsAlignment(public val amount: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.CELLS_ALIGNMENT, old, new)

    public class SkillXpPercent(public val amount: Float, public val skill: Skill, public val percent: Float, old: Component, new: Component) :
        ABWUE(ActionBarWidget.SKILL_XP, old, new)

    public class SkillXpLiteral(
        public val amount: Float,
        public val skill: Skill,
        public val current: Long,
        public val needed: Long,
        old: Component,
        new: Component,
    ) :
        ABWUE(ActionBarWidget.SKILL_XP_LITERAL, old, new)

    public class Unknown(widget: ActionBarWidget, old: Component, new: Component) : ABWUE(widget, old, new)
}

// <editor-fold desc="Deprecated Classes">
@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Health"))
public class HealthActionBarWidgetChangeEvent(public val current: Int, public val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.HEALTH, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Defense"))
public class DefenseActionBarWidgetChangeEvent(public val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DEFENSE, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Mana"))
public class ManaActionBarWidgetChangeEvent(public val current: Int, public val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.MANA, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.OverflowMana"))
public class OverflowManaActionBarWidgetChangeEvent(public val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.OVERFLOW_MANA, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.RiftTime"))
public class RiftTimeActionBarWidgetChangeEvent(public val time: Duration?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.RIFT_TIME, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Armadillo"))
public class ArmadilloActionBarWidgetChangeEvent(public val current: Float, public val max: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMADILLO, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.ArmorStack"))
public class ArmorStackActionBarWidgetChangeEvent(public val current: Int, public val type: ArmorStackType?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMOR_STACK, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Secrets"))
public class SecretsActionBarWidgetChangeEvent(public val current: Int, public val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SECRETS, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Drill"))
public class DrillActionBarWidgetChangeEvent(public val current: Int, public val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DRILL_FUEL, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Pressure"))
public class PressureActionBarWidgetChangeEvent(public val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.PRESSURE, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.SkillXpPercent"))
public class SkillXpPercentActionBarWidgetChangeEvent(public val amount: Float, public val skill: Skill?, public val percent: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SKILL_XP, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.SkillXpLiteral"))
public class SkillXpLiteralActionBarWidgetChangeEvent(
    public val amount: Float,
    public val skill: Skill?,
    public val current: Long,
    public val needed: Long,
    old: String,
    new: String,
) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SKILL_XP_LITERAL, old, new)
// </editor-fold>

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
