package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundEngine;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.minecraft.sounds.SoundPlayedEvent;
import tech.thatgravyboat.skyblockapi.hooks.SoundInstanceAccessor;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    @Inject(method = "playDelayed", at = @At("HEAD"), cancellable = true)
    public void playDelayer(CallbackInfo ci, @Local(argsOnly = true) SoundInstance soundInstance) {
        if (tryPlay(soundInstance)) {
            ci.cancel();
        }
    }

    @Inject(method = "play", at = @At("HEAD"), cancellable = true)
    public void play(CallbackInfoReturnable<SoundEngine.PlayResult> cir, @Local(argsOnly = true) SoundInstance soundInstance) {
        if (tryPlay(soundInstance)) {
            cir.setReturnValue(SoundEngine.PlayResult.NOT_STARTED);
        }
    }

    @Unique
    private static boolean tryPlay(SoundInstance soundInstance) {
        if (!(soundInstance instanceof SoundInstanceAccessor accessor)) return false;
        if (accessor.skyblockapi$getSoundEvent() == null) return false;

        var event = new SoundPlayedEvent(
            accessor.skyblockapi$getSoundEvent(),
            new Vec3(soundInstance.getX(), soundInstance.getY(), soundInstance.getZ()),
            accessor.skyblockapi$volume(),
            accessor.skyblockapi$pitch()
        );
        return event.post(SkyBlockAPI.getEventBus());
    }

}
