package tech.thatgravyboat.skyblockapi.hooks;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface GuiGraphicsHook {

    void skyblockapi$setHoveredItem(ItemStack stack);
}
