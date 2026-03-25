package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.item.ArmorStack as ArmorStackType
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI.Skill
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import kotlin.time.Duration

class RenderActionBarWidgetEvent(val widget: ActionBarWidget) : CancellableSkyBlockEvent()

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent"))
open class ActionBarWidgetChangeEvent(
    val widget: ActionBarWidget,
    val old: String,
    val new: String,
) : SkyBlockEvent()

private typealias ABWUE = ActionBarWidgetUpdateEvent

open class ActionBarWidgetUpdateEvent private constructor(val widget: ActionBarWidget, val old: Component, val new: Component) : SkyBlockEvent() {
    class Health(val current: Int, val max: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.HEALTH, old, new)
    class Defense(val current: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.DEFENSE, old, new)
    class Mana(val current: Int, val max: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.MANA, old, new)
    class NoMana(old: Component, new: Component) : ABWUE(ActionBarWidget.NO_MANA, old, new)
    class OverflowMana(val current: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.OVERFLOW_MANA, old, new)
    class RiftTime(val time: Duration, old: Component, new: Component) : ABWUE(ActionBarWidget.RIFT_TIME, old, new)
    class Armadillo(val current: Float, val max: Float, old: Component, new: Component) : ABWUE(ActionBarWidget.ARMADILLO, old, new)
    class ArmorStack(val current: Int, val type: ArmorStackType, old: Component, new: Component) : ABWUE(ActionBarWidget.ARMOR_STACK, old, new)
    class Secrets(val current: Int, val max: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.SECRETS, old, new)
    class Drill(val current: Int, val max: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.DRILL_FUEL, old, new)
    class Ability(val manaAmount: Int, val ability: Component, old: Component, new: Component) : ABWUE(ActionBarWidget.ABILITY, old, new)
    class Pressure(val current: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.PRESSURE, old, new)
    class SkyBlockXp(val gained: Long, old: Component, new: Component) : ABWUE(ActionBarWidget.SKYBLOCK_XP, old, new)
    class Charges(val current: Int, val max: Component, old: Component, new: Component) : ABWUE(ActionBarWidget.CHARGES, old, new)
    class Location(val location: Component, old: Component, new: Component) : ABWUE(ActionBarWidget.LOCATION, old, new)
    class CellsAlignment(val amount: Int, old: Component, new: Component) : ABWUE(ActionBarWidget.CELLS_ALIGNMENT, old, new)
    class SkillXpPercent(val amount: Float, val skill: Skill, val percent: Float, old: Component, new: Component) : ABWUE(ActionBarWidget.SKILL_XP, old, new)
    class SkillXpLiteral(val amount: Float, val skill: Skill, val current: Long, val needed: Long, old: Component, new: Component) :
        ABWUE(ActionBarWidget.SKILL_XP_LITERAL, old, new)

    class Unknown(widget: ActionBarWidget, old: Component, new: Component) : ABWUE(widget, old, new)
}

// <editor-fold desc="Deprecated Classes">
@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Health"))
class HealthActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.HEALTH, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Defense"))
class DefenseActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DEFENSE, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Mana"))
class ManaActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.MANA, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.OverflowMana"))
class OverflowManaActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.OVERFLOW_MANA, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.RiftTime"))
class RiftTimeActionBarWidgetChangeEvent(val time: Duration?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.RIFT_TIME, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Armadillo"))
class ArmadilloActionBarWidgetChangeEvent(val current: Float, val max: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMADILLO, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.ArmorStack"))
class ArmorStackActionBarWidgetChangeEvent(val current: Int, val type: ArmorStackType?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMOR_STACK, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Secrets"))
class SecretsActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SECRETS, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Drill"))
class DrillActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DRILL_FUEL, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.Pressure"))
class PressureActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.PRESSURE, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.SkillXpPercent"))
class SkillXpPercentActionBarWidgetChangeEvent(val amount: Float, val skill: Skill?, val percent: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SKILL_XP, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetUpdateEvent.SkillXpLiteral"))
class SkillXpLiteralActionBarWidgetChangeEvent(val amount: Float, val skill: Skill?, val current: Long, val needed: Long, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SKILL_XP_LITERAL, old, new)
// </editor-fold>

enum class ActionBarWidget {
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
    ;

    private val string = toFormattedName()
    override fun toString() = string
}
