//? >= 26.4 {
package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.inventory.SlotWidget;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.hooks.GuiGraphicsHook;

@Mixin(SlotWidget.class)
public class SlotWidgetMixin {

    @Shadow
    @Final
    private Slot slot;

    @WrapOperation(method = "extractTooltipForNextRenderPass", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/inventory/SlotWidget;updateTooltip()V"))
     public void updateTooltip(SlotWidget instance, Operation<Void> original, @Local(argsOnly = true, name = "graphics") GuiGraphicsExtractor graphics) {
        if (graphics instanceof GuiGraphicsHook hook) {
            hook.skyblockapi$setHoveredItem(this.slot.getItem());
        }
        original.call(instance);
     }

}
//? }
