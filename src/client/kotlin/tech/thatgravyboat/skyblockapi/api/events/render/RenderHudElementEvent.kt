package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyblockEvent

class RenderHudElementEvent(
    val element: HudElement,
    val graphics: GuiGraphics,
) : CancellableSkyblockEvent()

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
