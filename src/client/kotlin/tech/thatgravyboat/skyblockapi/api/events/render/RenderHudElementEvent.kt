package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

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
}
