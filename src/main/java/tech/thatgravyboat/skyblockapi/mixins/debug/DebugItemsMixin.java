package tech.thatgravyboat.skyblockapi.mixins.debug;

import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.core.Holder;
import net.minecraft.core.component.PatchedDataComponentMap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
//? < 26.1
//import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugAccessor;
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory;

@Mixin(ItemStack.class)
public class DebugItemsMixin implements ItemDebugAccessor {
    @Unique
    private Multimap<ItemDebugCategory, Component> skyblockapi$debug_map;

    @Override
    public void skyblockapi$addEntry(@NotNull ItemDebugCategory category, @NotNull Component entry) {
        if (skyblockapi$debug_map == null) {
            this.skyblockapi$debug_map = MultimapBuilder.hashKeys().hashSetValues().build();
        }
        this.skyblockapi$debug_map.put(category, entry);
    }

    @Override
    public Multimap<@NotNull ItemDebugCategory, @NotNull Component> skyblockapi$getEntries() {
        return this.skyblockapi$debug_map;
    }

    //~ if >= 26.1 'world/level/ItemLike' -> 'core/Holder'
    @WrapOperation(method = "copy", at = @At(value = "NEW", target = "(Lnet/minecraft/core/Holder;ILnet/minecraft/core/component/PatchedDataComponentMap;)Lnet/minecraft/world/item/ItemStack;"))
        //~ if >= 26.1 'ItemLike' -> 'Holder<Item>'
    private ItemStack skyblockapi$copy(Holder<Item> item, int count, PatchedDataComponentMap patch, Operation<ItemStack> operation) {
        var stack = operation.call(item, count, patch);
        if (stack != null && this.skyblockapi$debug_map != null) {
            var self = (DebugItemsMixin) (Object) stack;
            self.skyblockapi$debug_map = MultimapBuilder.hashKeys().linkedListValues().build();
            self.skyblockapi$debug_map.putAll(this.skyblockapi$debug_map);
        }
        return stack;
    }
}
