package tech.thatgravyboat.skyblockapi.mixins.tagattachments;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShapeRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.EntityRenderAccessor;
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityEvents;
import tech.thatgravyboat.skyblockapi.helpers.SkyBlockEntity;

import java.util.List;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "render", at = @At("HEAD"))
    public <S extends EntityRenderState> void render(
            CallbackInfo ci,
            @Local(argsOnly = true) S state,
            @Local(argsOnly = true) PoseStack poseStack,
            @Local(argsOnly = true) MultiBufferSource bufferSource
    ) {
        if (!EntityEvents.INSTANCE.getDebug()) {
            return;
        }
        final Entity entity = ((EntityRenderAccessor) state).skyblockapi$getSelf();
        if (entity == Minecraft.getInstance().crosshairPickEntity) {
            final List<Entity> attachedLines = SkyBlockEntity.getAttachedEntities(entity);
            poseStack.pushPose();
            for (Entity attachedLine : attachedLines) {
                final Vec3 subtract = attachedLine.position().subtract(entity.position());

                ShapeRenderer.renderVector(
                        poseStack,
                        bufferSource.getBuffer(RenderType.lines()),
                        Vec3.ZERO.toVector3f(),
                        subtract.add(0,0.1,0).normalize().scale(2), -1);
            }
            poseStack.popPose();
        }
    }

    @Inject(method = "extractRenderState", at = @At("HEAD"))
    public <T extends Entity, S extends EntityRenderState> void extractRenderState(
            CallbackInfo ci,
            @Local(argsOnly = true) T self,
            @Local(argsOnly = true) S state
    ) {
        ((EntityRenderAccessor) state).skyblockapi$setSelf(self);
    }

}
