package tech.thatgravyboat.skyblockapi.mixins;

import net.minecraft.client.resources.sounds.AbstractSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.hooks.SoundInstanceAccessor;

@Mixin(AbstractSoundInstance.class)
public class AbstractSoundInstanceMixin implements SoundInstanceAccessor {

    @Shadow
    protected float volume;
    @Shadow
    protected float pitch;
    @Unique
    private SoundEvent skyblockapi$sound;

    @Inject(method = "<init>(Lnet/minecraft/sounds/SoundEvent;Lnet/minecraft/sounds/SoundSource;Lnet/minecraft/util/RandomSource;)V", at = @At("TAIL"))
    public void init(SoundEvent soundEvent, SoundSource soundSource, RandomSource randomSource, CallbackInfo ci) {
        this.skyblockapi$sound = soundEvent;
    }

    @Override
    public SoundEvent skyblockapi$getSoundEvent() {
        return skyblockapi$sound;
    }

    @Override
    public void skyblockapi$setSoundEvent(SoundEvent event) {
        this.skyblockapi$sound = event;
    }

    @Override
    public float skyblockapi$volume() {
        return this.volume;
    }

    @Override
    public float skyblockapi$pitch() {
        return this.pitch;
    }
}
