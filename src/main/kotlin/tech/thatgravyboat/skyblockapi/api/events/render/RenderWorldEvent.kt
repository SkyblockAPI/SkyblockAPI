package tech.thatgravyboat.skyblockapi.api.events.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop

//? <= 26.1
//import net.minecraft.client.renderer.MultiBufferSource

sealed class RenderWorldEvent(
    val poseStack: PoseStack,
    val submitNodeCollector: SubmitNodeCollector,
    val cameraPosition: Vec3,
    var cameraRotation: Quaternionf,
    val partialTicks: Float,
) : SkyBlockEvent() {

    object Start : SkyBlockEvent()

    class AfterEntities(
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        cameraPosition: Vec3,
        cameraRotation: Quaternionf,
        partialTicks: Float,
    ) : RenderWorldEvent(
        poseStack,
        submitNodeCollector,
        cameraPosition,
        cameraRotation,
        partialTicks,
    )

    class AfterTranslucent(
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        cameraPosition: Vec3,
        cameraRotation: Quaternionf,
        partialTicks: Float,
    ) : RenderWorldEvent(
        poseStack,
        submitNodeCollector,
        cameraPosition,
        cameraRotation,
        partialTicks,
    )

    class CollectSubmits(
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        cameraPosition: Vec3,
        cameraRotation: Quaternionf,
    ) : RenderWorldEvent(
        poseStack,
        submitNodeCollector,
        cameraPosition,
        cameraRotation,
        0f,
    )

    fun pushPop(action: PoseStack.() -> Unit) = this.poseStack.pushPop(action)
    fun atCamera(action: PoseStack.() -> Unit) = pushPop {
        translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z)
        action()
    }
}
