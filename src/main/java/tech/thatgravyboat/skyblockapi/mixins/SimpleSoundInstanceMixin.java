package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.hooks.SoundInstanceAccessor;

@Mixin(SimpleSoundInstance.class)
public class SimpleSoundInstanceMixin {

    @Inject(method = "<init>(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;FFLnet/minecraft/util/RandomSource;ZILnet/minecraft/client/resources/sounds/SoundInstance$Attenuation;DDD)V", at = @At("TAIL"))
    private void init(CallbackInfo ci, @Local(argsOnly = true) SoundEvent soundEvent) {
        ((SoundInstanceAccessor) this).skyblockapi$setSoundEvent(soundEvent);
    }

}
