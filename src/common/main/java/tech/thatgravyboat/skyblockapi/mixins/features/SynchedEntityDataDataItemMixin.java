package tech.thatgravyboat.skyblockapi.mixins.features;

import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import tech.thatgravyboat.skyblockapi.hooks.DataItemHook;

@Mixin(SynchedEntityData.DataItem.class)
public class SynchedEntityDataDataItemMixin<T> implements DataItemHook<T> {

    @Shadow
    @Final
    private T initialValue;

    @Unique
    private T skyblockapi$serverValue;

    @Override
    public void skyblockapi$setServerValue(T value) {
        this.skyblockapi$serverValue = value;
    }

    @Override
    public T skyblockapi$getServerValue() {
        if (this.skyblockapi$serverValue == null) {
            return this.initialValue;
        }
        return this.skyblockapi$serverValue;
    }
}
