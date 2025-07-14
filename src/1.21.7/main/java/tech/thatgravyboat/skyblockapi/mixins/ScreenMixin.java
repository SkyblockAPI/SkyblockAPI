package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.utils.text.RunnableClickEvent;

@Mixin(Screen.class)
public class ScreenMixin {

    @Shadow
    protected Minecraft minecraft;

    @WrapOperation(method = "handleComponentClicked", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;handleClickEvent(Lnet/minecraft/client/Minecraft;Lnet/minecraft/network/chat/ClickEvent;)V"))
    private void handleComponentClickedError(Screen instance, Minecraft minecraft, ClickEvent event, Operation<Void> original) {
        if (event instanceof RunnableClickEvent runnable) {
            runnable.getRunnable().invoke();
        } else {
            original.call(instance, minecraft, event);
        }
    }
}
