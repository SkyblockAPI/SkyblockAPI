package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphicsExtractor
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

class RenderHudElementEvent(
    val element: HudElement,
    val graphics: GuiGraphicsExtractor?,
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
    CHAT,
    EFFECTS,
    ;

    private val string = toFormattedName()
    override fun toString() = string
}
