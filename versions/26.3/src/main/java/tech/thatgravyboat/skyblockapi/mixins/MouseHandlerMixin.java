package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.MouseButtonEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent;
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseReleasedEvent;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @WrapOperation(
        method = "onButton",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z")
    )
    private boolean mouseClicked(Screen screen, MouseButtonEvent mouseButtonEvent, boolean b, Operation<Boolean> original) {
        var pre = new ScreenMouseClickEvent.Pre(screen, mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
        var result = pre.post(SkyBlockAPI.getEventBus()) || original.call(screen, mouseButtonEvent, b);
        var post = new ScreenMouseClickEvent.Post(screen, mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
        if (result) {
            post.cancel();
        }
        return post.post(SkyBlockAPI.getEventBus()) || result;
    }

    @WrapOperation(
        method = "onButton",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseReleased(Lnet/minecraft/client/input/MouseButtonEvent;)Z")
    )
    private boolean mouseReleased(Screen instance, MouseButtonEvent mouseButtonEvent, Operation<Boolean> original) {
        var pre = new ScreenMouseReleasedEvent.Pre(instance, mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
        var result = pre.post(SkyBlockAPI.getEventBus()) || original.call(instance, mouseButtonEvent);
        var post = new ScreenMouseReleasedEvent.Post(instance, mouseButtonEvent.x(), mouseButtonEvent.y(), mouseButtonEvent.button());
        if (result) {
            post.cancel();
        }
        return post.post(SkyBlockAPI.getEventBus()) || result;
    }

}
