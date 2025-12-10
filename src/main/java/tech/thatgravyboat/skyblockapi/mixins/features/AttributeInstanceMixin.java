package tech.thatgravyboat.skyblockapi.mixins.features;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import tech.thatgravyboat.skyblockapi.hooks.AttributeInstanceHook;

@Mixin(AttributeInstance.class)
public class AttributeInstanceMixin implements AttributeInstanceHook {

    @Shadow
    private double baseValue;
    @Unique
    private double skyblockapi$serverValue = Double.NaN;

    @WrapMethod(method = "setBaseValue")
    private void onAssignValue(double value, Operation<Void> original) {
        original.call(value);
        this.skyblockapi$serverValue = value;
    }

    @Override
    public void skyblockapi$setServerValue(double value) {
        this.skyblockapi$serverValue = value;
    }

    @Override
    public double skyblockapi$getServerValue() {
        return Double.isNaN(this.skyblockapi$serverValue) ? this.baseValue : this.skyblockapi$serverValue;
    }
}
