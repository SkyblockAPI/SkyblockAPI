package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenBackgroundEvent;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent;
import tech.thatgravyboat.skyblockapi.utils.text.RunnableClickEvent;

@Mixin(Screen.class)
public class ScreenMixin {

    @WrapOperation(method = "renderWithTooltip", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V"))
    private void renderBefore(Screen instance, GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, Operation<Void> original) {
        if (!new RenderScreenBackgroundEvent((Screen) (Object) this, graphics).post(SkyBlockAPI.getEventBus())) {
            original.call(instance, graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Inject(
        method = "renderWithTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/Screen;render(Lnet/minecraft/client/gui/GuiGraphics;IIF)V",
            shift = At.Shift.AFTER
        )
    )
    private void renderAfter(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        new RenderScreenForegroundEvent((Screen) (Object) this, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapOperation(method = "handleComponentClicked", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V", remap = false))
    private void handleComponentClickedError(Logger instance, String string, Object o, Operation<Void> original, @Local(ordinal = 0) ClickEvent event) {
        if (event instanceof RunnableClickEvent runnable) {
            runnable.getRunnable().invoke();
        } else {
            original.call(instance, string, o);
        }
    }
}
