package tech.thatgravyboat.skyblockapi.mixins.fixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(targets = "net.minecraft.client.resources.SkinManager$1")
public class SkinManagerMixin {

    @WrapOperation(method = "method_54647", at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V"))
    private static void onSkinTextureAvailableCallback(Logger instance, String s, Object o, Operation<Void> original) {
        // Do nothing
    }
}
