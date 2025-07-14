package tech.thatgravyboat.skyblockapi.utils.extentions

import com.mojang.blaze3d.vertex.PoseStack

fun PoseStack.translate(x: Number, y: Number, z: Number) {
    this.translate(x.toFloat(), y.toFloat(), z.toFloat())
}

inline fun PoseStack.pushPop(action: PoseStack.() -> Unit) {
    this.pushPose()
    this.action()
    this.popPose()
}

inline fun PoseStack.translated(x: Number = 0, y: Number = 0, z: Number = 0, action: PoseStack.() -> Unit) {
    this.pushPop {
        this.translate(x.toFloat(), y.toFloat(), z.toFloat())
        this.action()
    }
}

inline fun PoseStack.scaled(x: Number = 1, y: Number = 1, z: Number = 1, action: PoseStack.() -> Unit) {
    this.pushPop {
        this.scale(x.toFloat(), y.toFloat(), z.toFloat())
        this.action()
    }
}
