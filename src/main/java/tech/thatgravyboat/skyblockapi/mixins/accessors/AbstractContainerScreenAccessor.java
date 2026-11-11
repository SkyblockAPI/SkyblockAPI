package tech.thatgravyboat.skyblockapi.mixins.accessors;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

//? if <26.4
//import net.minecraft.world.inventory.Slot;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenAccessor {

    //? < 26.4 {
    /*@Accessor("hoveredSlot")
    Slot getHoveredSlot();*///?}

    @Accessor("leftPos")
    int getLeftPos();

    @Accessor("topPos")
    int getTopPos();

    @Accessor("imageWidth")
    int getContainerWidth();

    @Accessor("imageHeight")
    int getContainerHeight();
}
