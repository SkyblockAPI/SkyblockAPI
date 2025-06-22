package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

class RenderHudElementEvent(
    val element: HudElement,
    val graphics: GuiGraphics?,
) : CancellableSkyBlockEvent()

enum class HudElement {
    HOTBAR,
    JUMP,
    EXPERIENCE,
    HEALTH,
    ARMOR,
    FOOD,
    AIR,

    SCOREBOARD,
    EFFECTS,
    ;

    private val string = toFormattedName()

    override fun toString() = string
}
