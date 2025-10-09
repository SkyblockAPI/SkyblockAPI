package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.entity.EntityEquipment;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerEquipmentChangeEvent;
import tech.thatgravyboat.skyblockapi.mixins.accessors.PlayerEquipmentAccessor;

import java.util.EnumMap;
import java.util.Objects;
import java.util.function.BiFunction;

@Mixin(EntityEquipment.class)
public class EntityEquipmentMixin {

    @Shadow
    @Final
    private EnumMap<EquipmentSlot, ItemStack> items;

    @ModifyReturnValue(at = @At("RETURN"), method = "set")
    public ItemStack set(ItemStack original, @Local(argsOnly = true) EquipmentSlot equipmentSlot, @Local(argsOnly = true) ItemStack itemStack) {
        if (this instanceof PlayerEquipmentAccessor playerEquipment) {
            new PlayerEquipmentChangeEvent(playerEquipment.skyblockapi$player(), equipmentSlot, original, itemStack).post(SkyBlockAPI.getEventBus());
        }
        return original;
    }

    @WrapOperation(method = "clear", at = @At(value = "INVOKE", target = "Ljava/util/EnumMap;replaceAll(Ljava/util/function/BiFunction;)V"))
    private void clear(EnumMap<EquipmentSlot, ItemStack> instance, BiFunction<EquipmentSlot, ItemStack, ItemStack> biFunction, Operation<Void> original) {
        original.call(
            instance, (BiFunction<EquipmentSlot, ItemStack, ItemStack>) (equipment, stack) -> {
                var returnValue = biFunction.apply(equipment, stack);
                if (this instanceof PlayerEquipmentAccessor playerEquipment) {
                    new PlayerEquipmentChangeEvent(playerEquipment.skyblockapi$player(), equipment, stack, returnValue).post(SkyBlockAPI.getEventBus());
                }
                return returnValue;
            });
    }

    @Inject(method = "setAll", at = @At("HEAD"))
    public void setAll(EntityEquipment other, CallbackInfo ci) {
        if (!(this instanceof PlayerEquipmentAccessor playerEquipment)) {
            return;
        }
        for (var slot : EquipmentSlot.values()) {
            var current = Objects.requireNonNullElse(items.get(slot), ItemStack.EMPTY);
            var next = Objects.requireNonNullElse(other.get(slot), ItemStack.EMPTY);
            if (current.isEmpty() && next.isEmpty()) {
                continue;
            }
            new PlayerEquipmentChangeEvent(playerEquipment.skyblockapi$player(), slot, current, next).post(SkyBlockAPI.getEventBus());
        }
    }
}
