package tech.thatgravyboat.skyblockapi.api.events.render

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.pushPop

public sealed class RenderWorldEvent(
    public val poseStack: PoseStack,
    public val submitNodeCollector: SubmitNodeCollector,
    public val cameraPosition: Vec3,
    public var cameraRotation: Quaternionf,
    public val partialTicks: Float,
) : SkyBlockEvent() {

    public object Start : SkyBlockEvent()

    public class AfterEntities(
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

    public class AfterTranslucent(
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

    public class CollectSubmits(
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

    public fun pushPop(action: PoseStack.() -> Unit): Unit = this.poseStack.pushPop(action)
    public fun atCamera(action: PoseStack.() -> Unit): Unit = pushPop {
        translate(-cameraPosition.x, -cameraPosition.y, -cameraPosition.z)
        action()
    }
}
