package tech.thatgravyboat.skyblockapi.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenBackgroundEvent;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent;
import tech.thatgravyboat.skyblockapi.hooks.RunnableClickEventHook;

@Mixin(Screen.class)
public class ScreenMixin {

    @Final
    @Shadow
    protected Minecraft minecraft;

    @Inject(method = "extractRenderStateWithTooltipAndSubtitles", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;nextStratum()V", ordinal = 0), cancellable = true)
    private void renderBefore(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        var screen = (Screen) (Object) this;
        if (new RenderScreenBackgroundEvent(screen, graphics).post(SkyBlockAPI.getEventBus())) {
            new RenderScreenForegroundEvent(screen, graphics).post(SkyBlockAPI.getEventBus());
            graphics.extractDeferredElements(mouseX, mouseY, partialTicks);
            ci.cancel();
        }
    }

    @Inject(
        method = "extractRenderStateWithTooltipAndSubtitles",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;extractDeferredElements(IIF)V"
        )
    )
    private void renderAfter(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        new RenderScreenForegroundEvent((Screen) (Object) this, graphics).post(SkyBlockAPI.getEventBus());
    }

    @Inject(method = "defaultHandleGameClickEvent", at = @At("HEAD"), cancellable = true)
    private static void handleComponentClickedError(ClickEvent clickEvent, Minecraft minecraft, Screen screen, CallbackInfo ci) {
        if (clickEvent instanceof RunnableClickEventHook runnableEvent) {
            var runnable = runnableEvent.skyblockapi$getRunnable();
            if (runnable != null) {
                runnable.run();
                ci.cancel();
            }
        }
    }
}
