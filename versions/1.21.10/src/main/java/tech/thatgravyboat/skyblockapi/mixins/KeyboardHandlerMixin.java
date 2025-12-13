package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent;
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyReleasedEvent;

@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    @WrapOperation(
        method = "keyPress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z")
    )
    private boolean keyPressed(Screen screen, KeyEvent keyEvent, Operation<Boolean> original) {
        var pre = new ScreenKeyPressedEvent.Pre(screen, keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers());
        var result = pre.post(SkyBlockAPI.getEventBus()) || original.call(screen, keyEvent);
        var post = new ScreenKeyPressedEvent.Post(screen, keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers());
        if (result) {
            post.cancel();
        }
        return post.post(SkyBlockAPI.getEventBus()) || result;
    }

    @WrapOperation(
        method = "keyPress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyReleased(Lnet/minecraft/client/input/KeyEvent;)Z")
    )
    private boolean keyReleased(Screen screen, KeyEvent keyEvent, Operation<Boolean> original) {
        var pre = new ScreenKeyReleasedEvent.Pre(screen, keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers());
        var result = pre.post(SkyBlockAPI.getEventBus()) || original.call(screen, keyEvent);
        var post = new ScreenKeyReleasedEvent.Post(screen, keyEvent.key(), keyEvent.scancode(), keyEvent.modifiers());
        if (result) {
            post.cancel();
        }
        return post.post(SkyBlockAPI.getEventBus()) || result;
    }


}
