package tech.thatgravyboat.skyblockapi.mixins.features;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import kotlin.Unit;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.datatype.DataType;
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypeItemStack;
import tech.thatgravyboat.skyblockapi.api.item.VisualItemAccessor;
import tech.thatgravyboat.skyblockapi.api.item.calculator.ItemValueCalculator;
import tech.thatgravyboat.skyblockapi.api.item.calculator.ItemValueItemStack;
import tech.thatgravyboat.skyblockapi.api.item.calculator.ItemValueResult;
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry;

import java.util.Map;

@Mixin(ItemStack.class)
public class ItemStackExtensionMixin implements DataTypeItemStack, ItemValueItemStack {

    private static final ThreadLocal<Unit> skyblockapi$COPYING = new ThreadLocal<>();
    private Map<DataType<?>, ?> skyblockapi$data = Map.of();
    private ItemValueResult skyblockapi$itemValueResult = null;

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("RETURN"))
    private void skyblockapi$init(ItemLike item, int count, PatchedDataComponentMap map, CallbackInfo ci) {
        if (skyblockapi$COPYING.get() == null) {
            skyblockapi$data = DataTypesRegistry.INSTANCE.getData((ItemStack) (Object) this);
        } else {
            skyblockapi$data = Map.of();
        }
    }

    @WrapOperation(method = "copy", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack skyblockapi$copy(ItemLike item, int count, PatchedDataComponentMap patch, Operation<ItemStack> operation) {
        skyblockapi$COPYING.set(Unit.INSTANCE);
        ItemStack stack = operation.call(item, count, patch);
        ((DataTypeItemStack) (Object) stack).skyblockapi$setTypes(this.skyblockapi$data);

        ItemStack visualItem = ((VisualItemAccessor) (Object) stack).skyblockapi$getVisualItem();
        if (visualItem != null) stack = visualItem;

        skyblockapi$COPYING.remove();
        return stack;
    }

    @Override
    public @Nullable <T> T skyblockapi$getType(@NotNull DataType<T> type) {
        return type.cast(skyblockapi$data.get(type));
    }

    @Override
    public @NotNull Map<DataType<?>, ?> skyblockapi$getTypes() {
        return this.skyblockapi$data;
    }

    @Override
    public void skyblockapi$setTypes(@NotNull Map<@NotNull DataType<?>, ?> types) {
        this.skyblockapi$data = types;
    }

    @Override
    public @NotNull ItemValueResult skyblockapi$getItemValueResult() {
        if (this.skyblockapi$itemValueResult == null) {
            this.skyblockapi$itemValueResult = ItemValueCalculator.calculateItemValue((ItemStack) (Object) this);
        }
        return this.skyblockapi$itemValueResult;
    }
}
