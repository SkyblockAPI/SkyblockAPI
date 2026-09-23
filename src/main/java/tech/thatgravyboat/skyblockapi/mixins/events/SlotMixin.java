package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent;
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerHotbarChangeEvent;
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerInventoryChangeEvent;
import tech.thatgravyboat.skyblockapi.helpers.McScreen;

import static tech.thatgravyboat.skyblockapi.api.events.screen.PlayerHotbarChangeEvent.FIRST_HOTBAR_SLOT;

@Mixin(Slot.class)
public abstract class SlotMixin {

    @WrapMethod(method = "set")
    private void skyblockapi$wrapSetItem(ItemStack itemStack, Operation<Void> original) {
        var slot = (Slot) (Object) this;
        var previousItem = slot.getItem();
        original.call(itemStack);
        var menuScreen = McScreen.INSTANCE.getAsMenu();
        var isInventory = slot.container instanceof Inventory;
        var slotIndex = slot.index;
        if (isInventory) {
            new PlayerInventoryChangeEvent(slot, itemStack, previousItem).post(SkyBlockAPI.getEventBus());
            if (slotIndex >= FIRST_HOTBAR_SLOT && slotIndex < FIRST_HOTBAR_SLOT + 9) {
                new PlayerHotbarChangeEvent(slot, itemStack, previousItem).post(SkyBlockAPI.getEventBus());
            }
        }
        if (menuScreen != null && (isInventory || menuScreen.getMenu().isValidSlotIndex(slotIndex))) {
            new InventoryChangeEvent(itemStack, slot, menuScreen.getTitle(), menuScreen.getMenu().slots, menuScreen, previousItem).post(SkyBlockAPI.getEventBus());

        }
    }
}
