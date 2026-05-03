package tech.thatgravyboat.skyblockapi.mixins.features;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.syncher.SynchedEntityData;
import org.spongepowered.asm.mixin.Mixin;
import tech.thatgravyboat.skyblockapi.hooks.DataItemHook;

@Mixin(SynchedEntityData.class)
public class SynchedEntityDataMixin {

    @WrapMethod(method = "assignValue")
    @SuppressWarnings("unchecked")
    private <T> void onAssignValue(SynchedEntityData.DataItem<T> item, SynchedEntityData.DataValue<?> value, Operation<Void> original) {
        original.call(item, value);
        if (item instanceof DataItemHook<?> hook) {
            ((DataItemHook<T>) hook).skyblockapi$setServerValue((T) value.value());
        }
    }
}
