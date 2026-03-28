//? if >= 1.21.11 {
package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.Font;
//~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.hooks.GuiGraphicsHook;

import java.util.List;
import java.util.Optional;

@Mixin(AbstractContainerScreen.class)
public class AbstractContainerScreenMixin {

    @WrapOperation(
        //~ if >= 26.1 'renderTooltip' -> 'extractTooltip'
        method = "extractTooltip",
        at = @At(
            value = "INVOKE",
            //~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/Identifier;)V"
        )
    )
    private void onRenderTooltip(
        //~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
        GuiGraphicsExtractor instance,
        final Font font,
        final List<Component> texts,
        final Optional<TooltipComponent> optionalImage,
        final int xo,
        final int yo,
        final @Nullable Identifier style,
        Operation<Void> original,
        @Local(ordinal = 0) ItemStack stack
    ) {
        if (instance instanceof GuiGraphicsHook hook) {
            hook.skyblockapi$setHoveredItem(stack);
        }
        original.call(instance, font, texts, optionalImage, xo, yo, style);
    }
}
//? }
