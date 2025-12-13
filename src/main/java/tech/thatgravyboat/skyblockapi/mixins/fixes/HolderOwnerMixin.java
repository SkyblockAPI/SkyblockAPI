package tech.thatgravyboat.skyblockapi.mixins.fixes;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.core.HolderOwner;
import org.spongepowered.asm.mixin.Mixin;
import tech.thatgravyboat.skyblockapi.utils.json.LenientHolderOwner;

@Mixin(HolderOwner.class)
@SuppressWarnings("unchecked")
public interface HolderOwnerMixin<T> {

    @WrapMethod(method = "canSerializeIn")
    private boolean canSerializeIn(HolderOwner<T> owner, Operation<Boolean> original) {
        return ((HolderOwner<T>) this) instanceof LenientHolderOwner<T> ||
            owner instanceof LenientHolderOwner<T> ||
            original.call(owner);
    }
}
