package tech.thatgravyboat.skyblockapi.api.events.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop

//? <= 26.1
//import net.minecraft.client.renderer.MultiBufferSource

public sealed class RenderWorldEvent(
    public val poseStack: PoseStack,
    //? <= 26.1
    //public val buffer: MultiBufferSource,
    public val submitNodeCollector: SubmitNodeCollector,
    public val cameraPosition: Vec3,
    public var cameraRotation: Quaternionf,
    public val partialTicks: Float,
) : SkyBlockEvent() {

    public object Start : SkyBlockEvent()

    public class AfterEntities(
        poseStack: PoseStack,
        //? <= 26.1
        //buffer: MultiBufferSource,
        submitNodeCollector: SubmitNodeCollector,
        cameraPosition: Vec3,
        cameraRotation: Quaternionf,
        partialTicks: Float,
    ) : RenderWorldEvent(
        poseStack,
        //? <= 26.1
        //buffer,
        submitNodeCollector,
        cameraPosition,
        cameraRotation,
        partialTicks,
    )

    public class AfterTranslucent(
        poseStack: PoseStack,
        //? <= 26.1
        //buffer: MultiBufferSource,
        submitNodeCollector: SubmitNodeCollector,
        cameraPosition: Vec3,
        cameraRotation: Quaternionf,
        partialTicks: Float,
    ) : RenderWorldEvent(
        poseStack,
        //? <= 26.1
        //buffer,
        submitNodeCollector,
        cameraPosition,
        cameraRotation,
        partialTicks,
    )

    public class CollectSubmits(
        poseStack: PoseStack,
        //? <= 26.1
        //buffer: MultiBufferSource,
        submitNodeCollector: SubmitNodeCollector,
        cameraPosition: Vec3,
        cameraRotation: Quaternionf,
    ) : RenderWorldEvent(
        poseStack,
        //? <= 26.1
        //buffer,
        submitNodeCollector,
        cameraPosition,
        cameraRotation,
        0f,
    )

    public fun pushPop(action: PoseStack.() -> Unit): Unit = this.poseStack.pushPop(action)
    public fun atCamera(action: PoseStack.() -> Unit): Unit = pushPop {
        translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z)
        action()
    }
}
