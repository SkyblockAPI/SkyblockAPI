package tech.thatgravyboat.skyblockapi.hooks;

import net.minecraft.sounds.SoundEvent;

public interface SoundInstanceAccessor {

    SoundEvent skyblockapi$getSoundEvent();
    void skyblockapi$setSoundEvent(SoundEvent event);
    float skyblockapi$volume();
    float skyblockapi$pitch();

}
