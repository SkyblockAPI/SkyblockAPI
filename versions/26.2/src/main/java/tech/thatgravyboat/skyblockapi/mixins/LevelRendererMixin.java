package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.state.level.LevelRenderState;
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

    @Inject(method = "renderLevel", at = @At("HEAD"))
    public void beforeAll(CallbackInfo ci) {
        RenderWorldEvent.Start.INSTANCE.post(SkyBlockAPI.getEventBus());
    }

    @Inject(method = "lambda$addMainPass$0", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;submitBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeStorage;)V"))
    public void afterEntities(
        CallbackInfo ci,
        @Local PoseStack poseStack,
        @Local(argsOnly = true) LevelRenderState levelRenderState
    ) {
        var deltaTracker = this.deltaTracker.get();
        if (deltaTracker == null) {
            return;
        }
        new RenderWorldEvent.AfterEntities(
            poseStack,
            levelRenderState.cameraRenderState.pos,
            levelRenderState.cameraRenderState.orientation,
            deltaTracker.getGameTimeDeltaPartialTick(false)
        ).post(SkyBlockAPI.getEventBus());
    }

    @Inject(method = "lambda$addMainPass$0", at = @At(value = "CONSTANT", args = "stringValue=translucentTerrain"))
    public void afterTranslucent(
        CallbackInfo ci,
        @Local PoseStack poseStack,
        @Local(argsOnly = true) LevelRenderState levelRenderState
    ) {
        var deltaTracker = this.deltaTracker.get();
        if (deltaTracker == null) {
            return;
        }
        new RenderWorldEvent.AfterTranslucent(
            poseStack,
            levelRenderState.cameraRenderState.pos,
            levelRenderState.cameraRenderState.orientation,
            deltaTracker.getGameTimeDeltaPartialTick(false)
        ).post(SkyBlockAPI.getEventBus());
    }

}
