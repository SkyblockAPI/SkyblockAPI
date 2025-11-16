package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix3x2fStack;
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

    @Unique
    private final ThreadLocal<ItemStack> skyblockapi$originalItem = new ThreadLocal<>();

    @Shadow
    public abstract void renderItem(ItemStack itemStack, int i, int j);

    @Shadow
    @Final
    private Matrix3x2fStack pose;

    @Shadow
    public abstract void nextStratum();

    @Shadow
    public abstract void fill(int i, int j, int k, int l, int color);

    @Inject(
        method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
        at = @At("HEAD")
    )
    private void skyblockapi$renderVisualItem(CallbackInfo ci, @Local(argsOnly = true) LocalRef<ItemStack> itemStack) {
        var visualItem = VisualItemAccessor.getVisualItemAccessor(itemStack.get()).skyblockapi$getVisualItem();
        if (visualItem == null) {
            return;
        }
        itemStack.set(visualItem);
    }

    @WrapMethod(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V")
    private void skyblockapi$renderItemDecorations(Font font, ItemStack itemStack, int i, int j, Operation<Void> original) {
        skyblockapi$originalItem.set(itemStack);
        var visualItem = VisualItemAccessor.getVisualItemAccessor(itemStack).skyblockapi$getVisualItem();
        var item = visualItem != null ? visualItem : itemStack;
        original.call(font, item, i, j);
        skyblockapi$originalItem.remove();
    }

    @Inject(method = "renderItemCount", at = @At("HEAD"))
    private void skyblockapi$renderItemCount(
        CallbackInfo ci,
        @Local(argsOnly = true) LocalRef<String> count,
        @Local(argsOnly = true) LocalRef<ItemStack> itemStack
    ) {
        var slotText = VisualItemAccessor.getVisualItemAccessor(itemStack.get()).skyblockapi$getSlotText();
        if (slotText != null) count.set(slotText);
        else {
            var item = skyblockapi$originalItem.get();
            if (item != null) itemStack.set(item);
        }
    }

    @Inject(
        method = "renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
        at = @At("HEAD")
    )
    private void skyblockapi$renderBackgroundItem(
        CallbackInfo ci,
        @Local(argsOnly = true) ItemStack itemStack,
        @Local(argsOnly = true, ordinal = 0) int x,
        @Local(argsOnly = true, ordinal = 1) int y,
        @Local(argsOnly = true, ordinal = 2) int z
    ) {
        var accessor = VisualItemAccessor.getVisualItemAccessor(itemStack);
        var backgroundItem = accessor.skyblockapi$getBackgroundItem();
        if (backgroundItem != null) {
            this.pose.pushMatrix();
            this.pose.translate(x, y);
            this.renderItem(backgroundItem, 0, 0);
            this.pose.popMatrix();

            this.nextStratum();
        }
        var color = accessor.skyblockapi$getBackgroundColor();
        if (color != null) {
            this.fill(x, y, x + 16, y + 16, color);
        }
    }

}
