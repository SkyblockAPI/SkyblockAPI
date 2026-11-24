package tech.thatgravyboat.skyblockapi.mixins.fixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

//~ if >= 26.3 'yggdrasil.YggdrasilServicesKeyInfo' -> 'services.MinecraftServicesKeyInfo'
@Mixin(value = com.mojang.authlib.services.MinecraftServicesKeyInfo.class, remap = false)
public class YggdrasilServicesKeyInfoMixin {

    @WrapOperation(
        method = "validateProperty",
        at = @At(
            value = "INVOKE",
            target = "Lorg/slf4j/Logger;error(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V"
        ),
        remap = false
    )
    private void onValidatePropertyError(Logger instance, String s, Object o1, Object o2, Operation<Void> original) {
        // Do nothing
    }

}
