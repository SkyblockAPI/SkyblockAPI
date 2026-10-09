package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.gui.GuiGraphicsExtractor
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

public class RenderHudElementEvent(
    public val element: HudElement,
    public val graphics: GuiGraphicsExtractor?,
) : CancellableSkyBlockEvent()

public enum class HudElement {
    HOTBAR,
    JUMP,
    EXPERIENCE,
    HEALTH,
    ABSORPTION_HEARTS,
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
