package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyboardHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent;
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyReleasedEvent;

@Debug(export = true)
@Mixin(KeyboardHandler.class)
public class KeyboardHandlerMixin {

    //? >= 26.4 {
    @Definition(id = "screen", local = @Local(type = Screen.class, name = "screen"))
    @Expression("screen != null")
    @ModifyExpressionValue(
        method = "keyPress",
        at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0)
    )
    private boolean keyPressed(boolean original, @Local(argsOnly = true, name = "event") KeyEvent keyEvent, @Local(name = "screen") Screen screen) {
        if (!original) return false;
        var pre = new ScreenKeyPressedEvent.Navigation(screen, keyEvent);
        return !pre.post(SkyBlockAPI.getEventBus());
    }//?}

    @WrapOperation(method = "keyPress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyPressed(Lnet/minecraft/client/input/KeyEvent;)Z"))
    private boolean keyPressed(Screen instance, KeyEvent event, Operation<Boolean> original) {
        boolean result = new ScreenKeyPressedEvent.Pre(instance, event).post(SkyBlockAPI.getEventBus()) || original.call(instance, event);
        var post = new ScreenKeyPressedEvent.Post(instance, event);
        if (result) post.cancel();
        return post.post(SkyBlockAPI.getEventBus()) || result;
    }

    @WrapOperation(
        method = "keyPress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;keyReleased(Lnet/minecraft/client/input/KeyEvent;)Z")
    )
    private boolean keyReleased(Screen screen, KeyEvent keyEvent, Operation<Boolean> original) {
        var pre = new ScreenKeyReleasedEvent.Pre(screen, keyEvent);
        var result = pre.post(SkyBlockAPI.getEventBus()) || original.call(screen, keyEvent);
        var post = new ScreenKeyReleasedEvent.Post(screen, keyEvent);
        if (result) {
            post.cancel();
        }
        return post.post(SkyBlockAPI.getEventBus()) || result;
    }
}
