package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenBackgroundEvent;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent;
import tech.thatgravyboat.skyblockapi.utils.text.RunnableClickEvent;

@Mixin(Screen.class)
public class ScreenMixin {

    @Shadow
    protected Minecraft minecraft;

    @Inject(method = "renderWithTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphics;nextStratum()V", ordinal = 0), cancellable = true)
    private void renderBefore(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        var screen = (Screen) (Object) this;
        if (new RenderScreenBackgroundEvent(screen, graphics).post(SkyBlockAPI.getEventBus())) {
            new RenderScreenForegroundEvent(screen, graphics).post(SkyBlockAPI.getEventBus());
            graphics.renderDeferredTooltip();
            ci.cancel();
        }
    }

    @Inject(
        method = "renderWithTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;renderDeferredTooltip()V"
        )
    )
    private void renderAfter(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        new RenderScreenForegroundEvent((Screen) (Object) this, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapOperation(method = "handleComponentClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;handleClickEvent(Lnet/minecraft/client/Minecraft;Lnet/minecraft/network/chat/ClickEvent;)V"))
    private void handleComponentClickedError(Screen instance, Minecraft minecraft, ClickEvent event, Operation<Void> original) {
        if (event instanceof RunnableClickEvent runnable) {
            runnable.getRunnable().invoke();
        } else {
            original.call(instance, minecraft, event);
        }
    }
}
