package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.renderpearl.api.commands.RenderPass;
import com.mojang.renderpearl.api.textures.GpuSampler;
import com.mojang.renderpearl.api.textures.GpuTextureView;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.SubmitNodeStorage;
import net.minecraft.client.renderer.chunk.ChunkSectionLayerGroup;
import net.minecraft.client.renderer.chunk.ChunkSectionsToRender;
import net.minecraft.client.renderer.state.level.LevelRenderState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent;

@Mixin(LevelRenderer.class)
public abstract class LevelRendererMixin {

    @Shadow
    @Final
    private LevelRenderState levelRenderState;

    @Shadow
    @Final
    private SubmitNodeStorage submitNodeStorage;

    @Shadow
    protected abstract void checkPoseStack(PoseStack poseStack);

    @Inject(method = "render", at = @At("HEAD"))
    public void beforeAll(CallbackInfo ci) {
        RenderWorldEvent.Start.INSTANCE.post(SkyBlockAPI.getEventBus());
    }

    @Inject(method = "submitFeatures", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;submitBlockEntities(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/state/level/LevelRenderState;Lnet/minecraft/client/renderer/SubmitNodeCollector;)V"))
    public void afterEntities(
        CallbackInfo ci,
        @Local(name = "poseStack") PoseStack poseStack
    ) {
        new RenderWorldEvent.AfterEntities(
            poseStack,
            this.submitNodeStorage,
            this.levelRenderState.cameraRenderState.pos,
            this.levelRenderState.cameraRenderState.orientation,
            this.levelRenderState.worldPartialTicks
        ).post(SkyBlockAPI.getEventBus());
    }

    @WrapOperation(
        method = "executeClassicTransparency",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/chunk/ChunkSectionsToRender;renderGroup(Lnet/minecraft/client/renderer/chunk/ChunkSectionLayerGroup;Lcom/mojang/renderpearl/api/commands/RenderPass;Lcom/mojang/renderpearl/api/textures/GpuSampler;Lcom/mojang/renderpearl/api/textures/GpuTextureView;Z)V"
        )
    )
    public void afterTranslucent(
        ChunkSectionsToRender instance,
        ChunkSectionLayerGroup group,
        RenderPass renderPass,
        GpuSampler sampler,
        GpuTextureView blockAtlas,
        boolean renderWireframeTerrain,
        Operation<Void> original
    ) {
        original.call(instance, group, renderPass, sampler, blockAtlas, renderWireframeTerrain);

        var poseStack = new PoseStack();
        new RenderWorldEvent.AfterTranslucent(
            poseStack,
            this.submitNodeStorage,
            this.levelRenderState.cameraRenderState.pos,
            this.levelRenderState.cameraRenderState.orientation,
            this.levelRenderState.worldPartialTicks
        ).post(SkyBlockAPI.getEventBus());
        this.checkPoseStack(poseStack);
    }
}
