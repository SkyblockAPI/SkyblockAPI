package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.item.VisualItemAccessor;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsVisualItemMixin {

    @Shadow
    @Final
    private PoseStack pose;

    @Shadow
    public abstract void renderItem(ItemStack itemStack, int i, int j);

    @Inject(method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V", at = @At("HEAD"))
    public void renderVisualItem(CallbackInfo ci, @Local(argsOnly = true) LocalRef<ItemStack> itemStack) {
        var visualItem = VisualItemAccessor.Companion.getVisualItemAccessor(itemStack.get()).skyblockapi$getVisualItem();
        if (visualItem == null) {
            return;
        }
        itemStack.set(visualItem);
    }

    @Unique
    private final ThreadLocal<ItemStack> skyblockapi$currentItem = new ThreadLocal<>();

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    private void renderVisualItemDecoration(CallbackInfo ci, @Local(argsOnly = true) LocalRef<ItemStack> itemStack) {
        var item = itemStack.get();
        skyblockapi$currentItem.set(item);
        var visualItem = VisualItemAccessor.Companion.getVisualItemAccessor(item).skyblockapi$getVisualItem();
        if (visualItem == null) {
            return;
        }
        itemStack.set(visualItem);
    }

    @Inject(
        method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;getItemModelResolver()Lnet/minecraft/client/renderer/item/ItemModelResolver;")
    )
    public void renderBackgroundItem(
        CallbackInfo ci,
        @Local(argsOnly = true) ItemStack itemStack,
        @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y,
        @Local(argsOnly = true, ordinal = 3) int z
    ) {
        var backgroundItem = VisualItemAccessor.Companion.getVisualItemAccessor(itemStack).skyblockapi$getBackgroundItem();
        if (backgroundItem != null) {
            this.pose.pushPose();
            this.pose.translate(x, y, z - 200);
            this.renderItem(backgroundItem, 0, 0);
            this.pose.popPose();
        }
    }

    @Inject(method = "renderItemCount", at = @At("HEAD"))
    public void renderItemCount(
        CallbackInfo ci,
        @Local(argsOnly = true) LocalRef<String> count,
        @Local(argsOnly = true) LocalRef<ItemStack> itemStack
    ) {
        var slotText = VisualItemAccessor.Companion.getVisualItemAccessor(itemStack.get()).skyblockapi$getSlotText();
        if (slotText != null) count.set(slotText);
        else {
            var item = skyblockapi$currentItem.get();
            if (item != null) itemStack.set(item);
        }
    }

}
