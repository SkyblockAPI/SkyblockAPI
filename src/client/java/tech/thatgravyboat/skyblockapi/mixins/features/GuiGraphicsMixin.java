package tech.thatgravyboat.skyblockapi.mixins.features;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.item.VisualItemAccessor;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin {

    @Shadow
    @Final
    private PoseStack pose;

    @Shadow
    public abstract void renderItem(ItemStack itemStack, int i, int j);

    @Inject(method =
        {
            "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
            "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
        }, at = @At("HEAD"))
    public void renderVisualItem(CallbackInfo ci, @Local(argsOnly = true) LocalRef<ItemStack> itemStack) {
        var visualItem = VisualItemAccessor.Companion.getVisualItemAccessor(itemStack.get()).skyblockapi$getVisualItem();
        if (visualItem == null) {
            return;
        }
        itemStack.set(visualItem);
    }

    @Inject(method = {
        "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
    }, at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;scale(FFF)V"))
    public void renderBackgroundItem(CallbackInfo ci, @Local(argsOnly = true) ItemStack itemStack) {
        var backgroundItem = VisualItemAccessor.Companion.getVisualItemAccessor(itemStack).skyblockapi$getBackgroundItem();
        if (backgroundItem != null) {
            int z = 200;
            this.pose.translate(-8, -8, -z);
            this.renderItem(backgroundItem, 0, 0);
            this.pose.translate(8, 8, z);
        }
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
