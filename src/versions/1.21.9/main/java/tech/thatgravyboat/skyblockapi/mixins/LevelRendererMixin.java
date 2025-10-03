package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.state.LevelRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Unique
    private final ThreadLocal<DeltaTracker> deltaTracker = new ThreadLocal<>();


    @Inject(method = "addMainPass", at = @At("HEAD"))
    public void saveDeltaTracker(CallbackInfo ci, @Local(argsOnly = true) DeltaTracker deltaTracker) {
        this.deltaTracker.set(deltaTracker);
    }

    @Inject(method = "lambda$addMainPass$1", at = @At(value = "CONSTANT", args = "stringValue=submitBlockEntities"))
    public void afterEntities(
        CallbackInfo ci,
        @Local PoseStack poseStack,
        @Local(ordinal = 0) MultiBufferSource.BufferSource source,
        @Local(argsOnly = true) LevelRenderState levelRenderState
    ) {
        var deltaTracker = this.deltaTracker.get();
        if (deltaTracker == null) {
            return;
        }
        new RenderWorldEvent.AfterEntities(
            poseStack,
            source,
            levelRenderState.cameraRenderState.pos,
            deltaTracker.getGameTimeDeltaPartialTick(false)
        ).post(SkyBlockAPI.getEventBus());
    }

    @Inject(method = "lambda$addMainPass$1", at = @At(value = "CONSTANT", args = "stringValue=string"))
    public void afterTranslucent(
        CallbackInfo ci,
        @Local PoseStack poseStack,
        @Local(ordinal = 0) MultiBufferSource.BufferSource source,
        @Local(argsOnly = true) LevelRenderState levelRenderState
    ) {
        var deltaTracker = this.deltaTracker.get();
        if (deltaTracker == null) {
            return;
        }
        new RenderWorldEvent.AfterEntities(
            poseStack,
            source,
            levelRenderState.cameraRenderState.pos,
            deltaTracker.getGameTimeDeltaPartialTick(false)
        ).post(SkyBlockAPI.getEventBus());
    }

}
