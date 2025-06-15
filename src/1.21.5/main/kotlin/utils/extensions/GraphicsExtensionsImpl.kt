package tech.thatgravyboat.skyblockapi.utils.extensions

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.GuiGraphics


inline fun GuiGraphics.pushPop(action: PoseStack.() -> Unit) {
    this.pose().pushPop(action)
}

inline fun GuiGraphics.scaled(x: Number = 1, y: Number = 1, z: Number = 1, action: PoseStack.() -> Unit) {
    this.pose().scaled(x, y, z, action)
}

inline fun GuiGraphics.translated(x: Number = 0, y: Number = 0, z: Number = 0, action: PoseStack.() -> Unit) {
    this.pose().translated(x, y, z, action)
}
