package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphics
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

class RenderHudElementEvent(
    val element: HudElement,
    val graphics: GuiGraphics,
) : SkyblockEvent()

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
