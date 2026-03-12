package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.Font;
//~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
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

//~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
@Mixin(GuiGraphicsExtractor.class)
public abstract class GuiGraphicsVisualItemMixin {

    @Unique
    private final ThreadLocal<ItemStack> skyblockapi$originalItem = new ThreadLocal<>();

    @Shadow
    public abstract void item(ItemStack itemStack, int i, int j);

    @Shadow
    @Final
    private Matrix3x2fStack pose;

    @Shadow
    public abstract void nextStratum();

    @Shadow
    public abstract void fill(int i, int j, int k, int l, int color);

    @Shadow
    public abstract void text(Font par1, Component par2, int par3, int par4, int par5, boolean par6);

    @Inject(
        method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
        at = @At("HEAD")
    )
    private void skyblockapi$renderVisualItem(CallbackInfo ci, @Local(argsOnly = true) LocalRef<ItemStack> itemStack) {
        var visualItem = VisualItemAccessor.getVisualItemAccessor(itemStack.get()).skyblockapi$getVisualItem();
        if (visualItem == null) {
            return;
        }
        itemStack.set(visualItem);
    }

    @WrapMethod(method = "itemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V")
    private void wrapRenderItemDecorations(Font font, ItemStack itemStack, int i, int j, String string, Operation<Void> original) {
        skyblockapi$originalItem.set(itemStack);
        var visualItem = VisualItemAccessor.getVisualItemAccessor(itemStack).skyblockapi$getVisualItem();
        var item = visualItem != null ? visualItem : itemStack;
        original.call(font, item, i, j, string);
        skyblockapi$originalItem.remove();
    }

    @Inject(method = "itemCount", at = @At("HEAD"))
    private void setComponentAndChangeItem(
        CallbackInfo ci,
        @Share("component") LocalRef<Component> componentRef,
        @Local(argsOnly = true) LocalRef<ItemStack> itemStack
    ) {
        var slotText = VisualItemAccessor.getVisualItemAccessor(itemStack.get()).skyblockapi$getSlotText();
        if (slotText != null) componentRef.set(slotText);
        else {
            var item = skyblockapi$originalItem.get();
            if (item != null) itemStack.set(item);
        }
    }

    @Definition(id = "string", local = @Local(type = String.class, argsOnly = true))
    @Expression("string != null")
    @WrapOperation(method = "itemCount", at = @At(value = "MIXINEXTRAS:EXPRESSION", ordinal = 0))
    private boolean wrapIsNullCheck(Object left, Object right, Operation<Boolean> original, @Share("component") LocalRef<Component> componentRef) {
        return original.call(left, right) || componentRef.get() != null;
    }

    // Ignore the warning about no possible signatures for this injector
    //~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
    @WrapOperation(method = "itemCount", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;text(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)V"))
    private void wrapIsNullCheck(
        //~ if >= 26.1 'GuiGraphics' -> 'GuiGraphicsExtractor'
        GuiGraphicsExtractor instance,
        Font font,
        String string,
        int i,
        int j,
        int k,
        boolean flag,
        Operation<Void> original,
        @Share("component") LocalRef<Component> componentRef,
        @Local(argsOnly = true, ordinal = 0) int original_i
    ) {
        var component = componentRef.get();
        if (component == null) original.call(instance, font, string, i, j, k, flag);
        else this.text(font, component, original_i + 19 - 2 - font.width(component), j, k, flag);
    }

    @Inject(
        method = "item(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;III)V",
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
        var color = accessor.skyblockapi$getBackgroundColor();
        if (color != 0) {
            this.fill(x, y, x + 16, y + 16, color);
        }
        var backgroundItem = accessor.skyblockapi$getBackgroundItem();
        if (backgroundItem != null) {
            this.pose.pushMatrix();
            this.pose.translate(x, y);
            this.item(backgroundItem, 0, 0);
            this.pose.popMatrix();

            this.nextStratum();
        }
    }

}
