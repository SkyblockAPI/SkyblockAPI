package tech.thatgravyboat.skyblockapi.api.events.render

import com.mojang.blaze3d.vertex.PoseStack
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.minecraft.client.Camera
import net.minecraft.client.renderer.MultiBufferSource
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop

open class RenderWorldEvent(val ctx: WorldRenderContext) : SkyBlockEvent() {
    class AfterEntities(ctx: WorldRenderContext) : RenderWorldEvent(ctx)
    class AfterTranslucent(ctx: WorldRenderContext) : RenderWorldEvent(ctx)

    val poseStack: PoseStack = ctx.matrixStack()!!
    val buffer: MultiBufferSource = ctx.consumers()!!
    val camera: Camera = ctx.camera()

    fun pushPop(action: PoseStack.() -> Unit) = this.poseStack.pushPop(action)
    fun atCamera(action: PoseStack.() -> Unit) = pushPop {
        translate(-camera.position.x, -camera.position.y, -camera.position.z)
        action()
    }
}
