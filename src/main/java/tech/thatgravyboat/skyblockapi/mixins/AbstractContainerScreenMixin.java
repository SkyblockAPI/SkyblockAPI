//? if >= 1.21.11 {
package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.hooks.GuiGraphicsHook;

import java.util.List;
import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @WrapOperation(
        method = "renderTooltip",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V"
        )
    )
    private void onRenderTooltip(
        GuiGraphics instance,
        Font font,
        List<Component> list,
        Optional<TooltipComponent> optional, int i, int j, Identifier resourceLocation, Operation<Void> original,
        @Local(ordinal = 0) ItemStack stack
    ) {
        if (instance instanceof GuiGraphicsHook hook) {
            hook.skyblockapi$setHoveredItem(stack);
        }
        original.call(instance, font, list, optional, i, j, resourceLocation);
    }

}
//? }
