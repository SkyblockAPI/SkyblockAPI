package tech.thatgravyboat.skyblockapi.mixins.features;

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
    private Map<DataType<?>, ?> skyblockapi$data = Map.of();

    @Inject(method = "<init>(Lnet/minecraft/world/level/ItemLike;ILnet/minecraft/core/component/PatchedDataComponentMap;)V", at = @At("RETURN"))
    private void skyblockapi$init(ItemLike item, int count, PatchedDataComponentMap map, CallbackInfo ci) {
        skyblockapi$data = DataTypesRegistry.INSTANCE.getData$skyblock_api_client((ItemStack) (Object) this);
    }

    @Override
    public @Nullable <T> T skyblockapi$getType(@NotNull DataType<T> type) {
        return type.cast(skyblockapi$data.get(type));
    }

    @Override
    public @NotNull Map<DataType<?>, ?> skyblockapi$getTypes() {
        return this.skyblockapi$data;
    }
}
