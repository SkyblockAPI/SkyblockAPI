package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent;
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyReleasedEvent;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @WrapOperation(
        method = "keyPress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(III)Z")
    )
    private boolean keyPressed(Screen screen, int keycode, int scancode, int modifiers, Operation<Boolean> original) {
        var pre = new ScreenKeyPressedEvent.Pre(screen, keycode, scancode, modifiers);
        var result = pre.post(SkyBlockAPI.getEventBus()) || original.call(screen, keycode, scancode, modifiers);
        new ScreenKeyPressedEvent.Post(screen, keycode, scancode, modifiers).post(SkyBlockAPI.getEventBus());
        return result;
    }

    @WrapOperation(
        method = "keyPress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyReleased(III)Z")
    )
    private boolean keyReleased(Screen screen, int keycode, int scancode, int modifiers, Operation<Boolean> original) {
        var pre = new ScreenKeyReleasedEvent.Pre(screen, keycode, scancode, modifiers);
        var result = pre.post(SkyBlockAPI.getEventBus()) || original.call(screen, keycode, scancode, modifiers);
        new ScreenKeyReleasedEvent.Post(screen, keycode, scancode, modifiers).post(SkyBlockAPI.getEventBus());
        return result;
    }


}
