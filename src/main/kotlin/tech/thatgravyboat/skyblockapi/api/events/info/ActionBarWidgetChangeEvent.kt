package tech.thatgravyboat.skyblockapi.api.events.info

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.item.ArmorStack
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI.Skill
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName
import kotlin.time.Duration

class RenderActionBarWidgetEvent(val widget: ActionBarWidget) : CancellableSkyBlockEvent()

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW"))
open class ActionBarWidgetChangeEvent(
    val widget: ActionBarWidget,
    val old: String,
    val new: String,
) : SkyBlockEvent()

private typealias ABWCE = ActionBarWidgetChangeEventNEW

sealed class ActionBarWidgetChangeEventNEW(val widget: ActionBarWidget, val old: String, val new: String) : SkyBlockEvent() {
    class Health(val current: Int, val max: Int, old: String, new: String) : ABWCE(ActionBarWidget.HEALTH, old, new)
    class Defense(val current: Int, old: String, new: String) : ABWCE(ActionBarWidget.DEFENSE, old, new)
    class Mana(val current: Int, val max: Int, old: String, new: String) : ABWCE(ActionBarWidget.MANA, old, new)
    class NoMana(old: String, new: String) : ABWCE(ActionBarWidget.NO_MANA, old, new)
    class OverflowMana(val current: Int, old: String, new: String) : ABWCE(ActionBarWidget.OVERFLOW_MANA, old, new)
    class RiftTime(val time: Duration?, old: String, new: String) : ABWCE(ActionBarWidget.RIFT_TIME, old, new)
    class Armadillo(val current: Float, val max: Float, old: String, new: String) : ABWCE(ActionBarWidget.ARMADILLO, old, new)
    class ArmorStackWIDGET(val current: Int, val type: ArmorStack?, old: String, new: String) : ABWCE(ActionBarWidget.ARMOR_STACK, old, new)
    class Secrets(val current: Int, val max: Int, old: String, new: String) : ABWCE(ActionBarWidget.SECRETS, old, new)
    class Drill(val current: Int, val max: Int, old: String, new: String) : ABWCE(ActionBarWidget.DRILL_FUEL, old, new)
    class Ability(val manaAmount: Int, val ability: String, old: String, new: String) : ABWCE(ActionBarWidget.ABILITY, old, new)
    class Pressure(val current: Int, old: String, new: String) : ABWCE(ActionBarWidget.PRESSURE, old, new)
    class SkyBlockXp(val gained: Long, old: String, new: String) : ABWCE(ActionBarWidget.SKYBLOCK_XP, old, new)
    class Charges(val current: Int, val max: String, old: String, new: String) : ABWCE(ActionBarWidget.CHARGES, old, new)
    class Location(val location: String, old: String, new: String) : ABWCE(ActionBarWidget.LOCATION, old, new)
    class CellsAlignment(val amount: Int, old: String, new: String) : ABWCE(ActionBarWidget.CELLS_ALIGNMENT, old, new)
    class SkillXpPercent(val amount: Float, val skill: Skill?, val percent: Float, old: String, new: String) :
        ABWCE(ActionBarWidget.SKILL_XP, old, new)

    class SkillXpLiteral(val amount: Float, val skill: Skill?, val current: Long, val needed: Long, old: String, new: String) :
        ABWCE(ActionBarWidget.SKILL_XP_LITERAL, old, new)

    class Unknown(widget: ActionBarWidget, old: String, new: String) : ABWCE(widget, old, new)
}


// <editor-fold desc="Deprecated Classes">
@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.Health"))
class HealthActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.HEALTH, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.Defense"))
class DefenseActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DEFENSE, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.Mana"))
class ManaActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.MANA, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.OverflowMana"))
class OverflowManaActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.OVERFLOW_MANA, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.RiftTime"))
class RiftTimeActionBarWidgetChangeEvent(val time: Duration?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.RIFT_TIME, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.Armadillo"))
class ArmadilloActionBarWidgetChangeEvent(val current: Float, val max: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMADILLO, old, new)

class ArmorStackActionBarWidgetChangeEvent(val current: Int, val type: ArmorStack?, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.ARMOR_STACK, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.Secrets"))
class SecretsActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SECRETS, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.Drill"))
class DrillActionBarWidgetChangeEvent(val current: Int, val max: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.DRILL_FUEL, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.Pressure"))
class PressureActionBarWidgetChangeEvent(val current: Int, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.PRESSURE, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.SkillXpPercent"))
class SkillXpPercentActionBarWidgetChangeEvent(val amount: Float, val skill: Skill?, val percent: Float, old: String, new: String) :
    ActionBarWidgetChangeEvent(ActionBarWidget.SKILL_XP, old, new)

@RemoveNextVersion(ReplaceWith("ActionBarWidgetChangeEventNEW.SkillXpLiteral"))
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
