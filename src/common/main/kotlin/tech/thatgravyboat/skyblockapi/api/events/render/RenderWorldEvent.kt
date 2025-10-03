package tech.thatgravyboat.skyblockapi.api.events.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop

sealed class RenderWorldEvent(
    val poseStack: PoseStack,
    val buffer: MultiBufferSource,
    val cameraPosition: Vec3,
    var cameraRotation: Quaternionf,
    val partialTicks: Float,
) : SkyBlockEvent() {

    class AfterEntities(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        cameraPosition: Vec3,
        cameraRotation: Quaternionf,
        partialTicks: Float,
    ) : RenderWorldEvent(poseStack, buffer, cameraPosition, cameraRotation, partialTicks)

    class AfterTranslucent(
        poseStack: PoseStack,
        buffer: MultiBufferSource,
        cameraPosition: Vec3,
        cameraRotation: Quaternionf,
        partialTicks: Float,
    ) : RenderWorldEvent(poseStack, buffer, cameraPosition, cameraRotation, partialTicks)

    fun pushPop(action: PoseStack.() -> Unit) = this.poseStack.pushPop(action)
    fun atCamera(action: PoseStack.() -> Unit) = pushPop {
        translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z)
        action()
    }
}
