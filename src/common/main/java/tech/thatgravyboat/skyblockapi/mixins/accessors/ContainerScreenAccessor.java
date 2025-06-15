package tech.thatgravyboat.skyblockapi.mixins.accessors;

import net.minecraft.client.gui.screens.inventory.ContainerScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ContainerScreen.class)
public interface ContainerScreenAccessor {

    @Accessor("containerRows")
    int getContainerRows();

}
