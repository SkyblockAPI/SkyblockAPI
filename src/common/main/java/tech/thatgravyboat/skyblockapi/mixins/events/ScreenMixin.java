package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenBackgroundEvent;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderScreenForegroundEvent;

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
}
