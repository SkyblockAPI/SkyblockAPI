package tech.thatgravyboat.skyblockapi.mixins.features;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.skyblockapi.api.datatype.DataType;
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypeItemStack;
import tech.thatgravyboat.skyblockapi.api.item.VisualItemAccessor;
import tech.thatgravyboat.skyblockapi.api.item.calculator.ItemValueCalculator;
import tech.thatgravyboat.skyblockapi.api.item.calculator.ItemValueItemStack;
import tech.thatgravyboat.skyblockapi.api.item.calculator.ItemValueResult;
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry;

import java.util.Map;
import java.util.Objects;

@Mixin(ItemStack.class)
public class ItemStackExtensionMixin implements DataTypeItemStack, ItemValueItemStack {

    @Unique
    private static final ThreadLocal<Boolean> skyblockapi$COPYING = new ThreadLocal<>();
    @Unique
    private @Nullable Map<DataType<?>, ?> skyblockapi$data = Map.of();
    @Unique
    private ItemValueResult skyblockapi$itemValueResult = null;
    @Unique
    private boolean skyblockapi$dataIsDirty = false;


    @Inject(
        //~ if >= 26.1 'world/level/ItemLike' -> 'core/Holder'
        method = "<init>(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/PatchedDataComponentMap;)V",
        at = @At("RETURN")
    )
    private void skyblockapi$init(CallbackInfo ci) {
        if (skyblockapi$COPYING.get() == Boolean.FALSE) {
            skyblockapi$data = DataTypesRegistry.INSTANCE.getData((ItemStack) (Object) this);
        } else {
            skyblockapi$data = Map.of();
        }
    }

    //~ if >= 26.1 'world/level/ItemLike' -> 'core/Holder'
    @WrapOperation(method = "copy", at = @At(value = "NEW", target = "(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/PatchedDataComponentMap;)Lnet/minecraft/world/item/ItemStack;"))
    //~ if >= 26.1 'ItemLike' -> 'Holder<Item>'
    private ItemStack skyblockapi$copy(Holder<Item> item, int count, PatchedDataComponentMap patch, Operation<ItemStack> operation) {
        skyblockapi$COPYING.set(Boolean.TRUE);
        var stack = operation.call(item, count, patch);
        if (stack != null) {
            var self = (ItemStackExtensionMixin) (Object) stack;
            self.skyblockapi$data = this.skyblockapi$data;
            self.skyblockapi$dataIsDirty = this.skyblockapi$dataIsDirty;

            ((VisualItemAccessor) (Object) stack).skyblockapi$setVisualItem(((VisualItemAccessor) this).skyblockapi$getVisualItem());
        }

        skyblockapi$COPYING.set(Boolean.FALSE);
        return stack;
    }

    @Inject(method = {"set*", "remove"}, at = @At("HEAD"))
    private <T> void skyblockapi$dataChange(CallbackInfoReturnable<T> cir) {
        this.skyblockapi$dataIsDirty = true;
    }

    @Inject(method = {"applyComponents*", "applyComponentsAndValidate"}, at = @At("HEAD"))
    private void skyblockapi$dataChange(CallbackInfo ci) {
        this.skyblockapi$dataIsDirty = true;
    }

    @Override
    public @Nullable <T> T skyblockapi$getType(@NotNull DataType<T> type) {
        return type.cast(skyblockapi$getTypes().get(type));
    }

    @Override
    public @NotNull Map<DataType<?>, ?> skyblockapi$getTypes() {
        if (this.skyblockapi$dataIsDirty || this.skyblockapi$data == null) {
            this.skyblockapi$data = DataTypesRegistry.INSTANCE.getData((ItemStack) (Object) this);
            this.skyblockapi$dataIsDirty = false;
        }
        return Objects.requireNonNullElse(this.skyblockapi$data, Map.of());
    }

    @Override
    public @NotNull ItemValueResult skyblockapi$getItemValueResult() {
        if (this.skyblockapi$itemValueResult == null || this.skyblockapi$dataIsDirty) {
            this.skyblockapi$itemValueResult = ItemValueCalculator.calculateItemValue((ItemStack) (Object) this);
        }
        return this.skyblockapi$itemValueResult;
    }
}
