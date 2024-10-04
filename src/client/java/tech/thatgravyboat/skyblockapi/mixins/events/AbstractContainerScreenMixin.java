package tech.thatgravyboat.skyblockapi.mixins.events;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryFullyLoadedEvent;

import java.util.List;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin {
    @Inject(method = "init", at = @At("HEAD"))
    public void onOpenInventory(CallbackInfo info) {
        AbstractContainerScreen<?> screen = (AbstractContainerScreen<?>) (Object) this;

        List<ItemStack> containerItems = screen.getMenu().slots.stream()
            .map(Slot::getItem)
            .filter(stack -> !stack.isEmpty())
            .toList();

        new InventoryFullyLoadedEvent(containerItems, screen.getTitle()).post(SkyBlockAPI.getEventBus());
        System.out.println("InventoryFullyLoadedEvent posted");
    }
}
