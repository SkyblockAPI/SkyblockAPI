package tech.thatgravyboat.skyblockapi.mixins.accessors;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(SynchedEntityData.class)
public interface SynchedEntityDataAccessor {

    @Invoker("getItem")
    <T> SynchedEntityData.DataItem<T> skyblockapi$getItem(EntityDataAccessor<T> accessor);
}
