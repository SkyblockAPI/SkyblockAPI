package tech.thatgravyboat.skyblockapi.mixins.events;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent;
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerHotbarChangeEvent;
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerInventoryChangeEvent;
import tech.thatgravyboat.skyblockapi.helpers.McScreen;

@Mixin(Slot.class)
public class SlotMixin {

    @Unique
    private static final int FIRST_HOTBAR_SLOT = 36;

    @Inject(method = "set", at = @At("HEAD"))
    public void set(ItemStack itemStack, CallbackInfo ci) {
        var slot = (Slot) (Object) this;
        var menuScreen = McScreen.INSTANCE.getAsMenu();
        var isInventory = slot.container instanceof Inventory;
        var slotIndex = slot.index;
        if (isInventory) {
            new PlayerInventoryChangeEvent(slotIndex, itemStack).post(SkyBlockAPI.getEventBus());
            if (slotIndex >= FIRST_HOTBAR_SLOT && slotIndex < FIRST_HOTBAR_SLOT + 9) {
                new PlayerHotbarChangeEvent(slotIndex - FIRST_HOTBAR_SLOT, itemStack).post(SkyBlockAPI.getEventBus());
            }
        }
        if (menuScreen != null && (isInventory || menuScreen.getMenu().isValidSlotIndex(slotIndex))) {
            new InventoryChangeEvent(itemStack, slot, menuScreen.getTitle(), menuScreen.getMenu().slots, menuScreen).post(SkyBlockAPI.getEventBus());

        }
    }

}
