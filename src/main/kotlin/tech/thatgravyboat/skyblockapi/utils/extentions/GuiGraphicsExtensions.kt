package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.client.gui.GuiGraphicsExtractor
import tech.thatgravyboat.skyblockapi.platform.pushPop
import tech.thatgravyboat.skyblockapi.platform.scale
import tech.thatgravyboat.skyblockapi.platform.translate

inline fun GuiGraphicsExtractor.scissor(x: Int, y: Int, width: Int, height: Int, action: () -> Unit) {
    this.enableScissor(x, y, x + width, y + height)
    action()
    this.disableScissor()
}

inline fun GuiGraphicsExtractor.scissor(x: IntRange, y: IntRange, action: () -> Unit) {
    this.enableScissor(x.start, y.start, x.endInclusive, y.endInclusive)
    action()
    this.disableScissor()
}

inline fun GuiGraphicsExtractor.translated(x: Number = 0, y: Number = 0, action: () -> Unit) {
    this.pushPop {
        this.translate(x.toFloat(), y.toFloat())
        action()
    }
}

inline fun GuiGraphicsExtractor.scaled(x: Number = 1, y: Number = 1, action: () -> Unit) {
    this.pushPop {
        this.scale(x.toFloat(), y.toFloat())
        action()
    }
}
