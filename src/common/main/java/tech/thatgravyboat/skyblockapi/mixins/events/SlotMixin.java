package tech.thatgravyboat.skyblockapi.mixins.events;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent;
import tech.thatgravyboat.skyblockapi.helpers.McScreen;

@Mixin(Slot.class)
public class SlotMixin {

    @Inject(method = "set", at = @At("HEAD"))
    public void set(ItemStack itemStack, CallbackInfo ci) {
        var slot = (Slot) (Object) this;
        var self = McScreen.INSTANCE.getAsMenu();
        if (self == null) return;
        if (!(slot.container instanceof Inventory) && !self.getMenu().isValidSlotIndex(slot.index)) return;

        new InventoryChangeEvent(itemStack, slot, self.getTitle(), self.getMenu().slots, self).post$skyblock_api_client();
    }

}
