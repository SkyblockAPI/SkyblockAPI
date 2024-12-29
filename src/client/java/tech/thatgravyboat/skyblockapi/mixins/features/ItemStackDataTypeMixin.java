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
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.datatype.DataType;
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypeItemStack;
import tech.thatgravyboat.skyblockapi.impl.DataTypesRegistry;

import java.util.Map;

@Mixin(ItemStack.class)
public class ItemStackDataTypeMixin implements DataTypeItemStack {

    @Unique
    private static final ThreadLocal<Unit> COPYING = new ThreadLocal<>();

    @Unique
    private Map<DataType<?>, ?> skyblockapi$data = Map.of();

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("RETURN"))
    private void skyblockapi$init(ItemLike item, int count, PatchedDataComponentMap map, CallbackInfo ci) {
        if (COPYING.get() != null) return;
        skyblockapi$data = DataTypesRegistry.INSTANCE.getData$skyblock_api_client((ItemStack) (Object) this);
    }

    @WrapOperation(method = "copy", at = @At(value = "NEW", target = "(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)Lnet/minecraft/world/item/ItemStack;"))
    private ItemStack skyblockapi$copy(ItemLike item, int count, PatchedDataComponentMap patch, Operation<ItemStack> operation) {
        COPYING.set(Unit.INSTANCE);
        ItemStack stack = operation.call(item, count, patch);
        ((DataTypeItemStack) (Object) stack).skyblockapi$setTypes(this.skyblockapi$data);
        COPYING.remove();
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
}
