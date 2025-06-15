package tech.thatgravyboat.skyblockapi.mixins.features;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.utils.text.RunnableClickEvent;

@Mixin(Screen.class)
public class ScreenMixin {

    @WrapOperation(method = "handleComponentClicked", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;)V"))
    private void handleComponentClickedError(Logger instance, String string, Object o, Operation<Void> original, @Local(ordinal = 0) ClickEvent event) {
        if (event instanceof RunnableClickEvent runnable) {
            runnable.getRunnable().invoke();
        } else {
            original.call(instance, string, o);
        }
    }
}
