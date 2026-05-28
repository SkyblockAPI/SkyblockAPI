package tech.thatgravyboat.skyblockapi.api.events.render

import com.mojang.blaze3d.vertex.PoseStack
//? <= 26.1
//import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop

sealed class RenderWorldEvent(
    val poseStack: PoseStack,
    //? <= 26.1
    //val buffer: MultiBufferSource,
    val cameraPosition: Vec3,
    var cameraRotation: Quaternionf,
    val partialTicks: Float,
) : SkyBlockEvent() {

    object Start : SkyBlockEvent()

    class AfterEntities(
        poseStack: PoseStack,
        //? <= 26.1
        //buffer: MultiBufferSource,
        cameraPosition: Vec3,
        cameraRotation: Quaternionf,
        partialTicks: Float,
    ) : RenderWorldEvent(
        poseStack,
        //? <= 26.1
        //buffer,
        cameraPosition,
        cameraRotation,
        partialTicks,
    )

    class AfterTranslucent(
        poseStack: PoseStack,
        //? <= 26.1
        //buffer: MultiBufferSource,
        cameraPosition: Vec3,
        cameraRotation: Quaternionf,
        partialTicks: Float,
    ) : RenderWorldEvent(
        poseStack,
        //? <= 26.1
        //buffer,
        cameraPosition,
        cameraRotation,
        partialTicks,
    )

    fun pushPop(action: PoseStack.() -> Unit) = this.poseStack.pushPop(action)
    fun atCamera(action: PoseStack.() -> Unit) = pushPop {
        translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z)
        action()
    }
}
