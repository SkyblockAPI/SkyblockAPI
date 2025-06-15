package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseClickEvent;
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenMouseReleasedEvent;

@Mixin(MouseHandler.class)
public class MouseHandlerMixin {

    @WrapOperation(
        method = "onPress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseClicked(DDI)Z")
    )
    private boolean mouseClicked(Screen screen, double mouseX, double mouseY, int button, Operation<Boolean> original) {
        var pre = new ScreenMouseClickEvent.Pre(screen, mouseX, mouseY, button);
        var result = pre.post(SkyBlockAPI.getEventBus()) || original.call(screen, mouseX, mouseY, button);
        var post = new ScreenMouseClickEvent.Post(screen, mouseX, mouseY, button);
        if (result) post.cancel();
        return post.post(SkyBlockAPI.getEventBus()) || result;
    }

    @WrapOperation(
        method = "onPress",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;mouseReleased(DDI)Z")
    )
    private boolean mouseReleased(Screen instance, double mouseX, double mouseY, int button, Operation<Boolean> original) {
        var pre = new ScreenMouseReleasedEvent.Pre(instance, mouseX, mouseY, button);
        var result = pre.post(SkyBlockAPI.getEventBus()) || original.call(instance, mouseX, mouseY, button);
        var post = new ScreenMouseReleasedEvent.Post(instance, mouseX, mouseY, button);
        if (result) post.cancel();
        return post.post(SkyBlockAPI.getEventBus()) || result;
    }

}
