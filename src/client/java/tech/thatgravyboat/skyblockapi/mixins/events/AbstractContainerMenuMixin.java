package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.core.NonNullList;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent;
import tech.thatgravyboat.skyblockapi.helpers.McScreen;

@Mixin(AbstractContainerMenu.class)
public class AbstractContainerMenuMixin {

    @Shadow
    @Final
    public int containerId;

    @Shadow
    @Final
    public NonNullList<Slot> slots;

    @WrapOperation(method = {"initializeContents", "setItem"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/world/inventory/Slot;set(Lnet/minecraft/world/item/ItemStack;)V"))
    public void setItemEvent(Slot instance, ItemStack itemStack, Operation<Void> original) {
        original.call(instance, itemStack);
        final AbstractContainerScreen<?> asMenu = McScreen.INSTANCE.getAsMenu();
        if (asMenu == null) {
            return;
        }
        if (asMenu.getMenu().containerId != containerId) {
            return;
        }
        new InventoryChangeEvent(itemStack, instance, asMenu.getTitle(), slots, asMenu).post$skyblock_api_client();
    }

}
