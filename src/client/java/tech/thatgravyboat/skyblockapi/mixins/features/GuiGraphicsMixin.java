package tech.thatgravyboat.skyblockapi.mixins.features;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.item.VisualItemAccessor;

@Mixin(GuiGraphics.class)
public class GuiGraphicsMixin {

    @Inject(method =
        {
            "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
            "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
        }, at = @At("HEAD"))
    public void renderItem(CallbackInfo ci, @Local(argsOnly = true) LocalRef<ItemStack> itemStack) {
        var visualItem = VisualItemAccessor.Companion.getVisualItemAccessor(itemStack.get()).skyblockapi$getVisualItem();
        if (visualItem == null) {
            return;
        }
        itemStack.set(visualItem);
    }

    @Inject(method = "renderItemCount", at = @At("HEAD"))
    public void renderItemCount(
        CallbackInfo ci,
        @Local(argsOnly = true) LocalRef<String> count,
        @Local(argsOnly = true) ItemStack itemStack
    ) {
        var slotText = VisualItemAccessor.Companion.getVisualItemAccessor(itemStack).skyblockapi$getSlotText();
        if (slotText == null) {
            return;
        }
        count.set(slotText);
    }

}
