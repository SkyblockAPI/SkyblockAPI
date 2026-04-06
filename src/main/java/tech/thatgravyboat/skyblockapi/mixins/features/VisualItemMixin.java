package tech.thatgravyboat.skyblockapi.mixins.features;

import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.skyblockapi.api.item.ClickConsumer;
import tech.thatgravyboat.skyblockapi.api.item.VisualItemAccessor;

@Mixin(ItemStack.class)
public abstract class VisualItemMixin implements VisualItemAccessor {
    @Shadow
    public abstract boolean isEmpty();

    @Unique
    private @Nullable ItemStack visualItem;
    @Unique
    private @Nullable Component slotText;
    @Unique
    private @Nullable ClickConsumer clickAction;
    @Unique
    private @Nullable ItemStack backgroundItem;
    @Unique
    private int backgroundColor;
    @Unique
    private int foregroundColor;
    @Unique
    private int borderColor;

    @Override
    public void skyblockapi$setVisualItem(@Nullable ItemStack item) {
        if (this.isEmpty()) {
            return;
        }
        this.visualItem = item;
    }

    @Override
    public @Nullable ItemStack skyblockapi$getVisualItem() {
        return visualItem;
    }

    @Inject(method = "getTooltipLines", at = @At("HEAD"), cancellable = true)
    public void getTooltipLines(
        Item.TooltipContext tooltipContext,
        @Nullable Player player,
        TooltipFlag tooltipFlag,
        CallbackInfoReturnable<List<Component>> cir
    ) {
        if (visualItem == null) {
            return;
        }
        cir.setReturnValue(visualItem.getTooltipLines(tooltipContext, player, tooltipFlag));
    }

    @Override
    public void skyblockapi$setSlotText(@Nullable Component item) {
        this.slotText = item;
    }

    @Override
    public @Nullable Component skyblockapi$getSlotText() {
        return this.slotText;
    }

    @Override
    public void skyblockapi$setOnClickAction(@Nullable ClickConsumer clickAction) {
        this.clickAction = clickAction;
    }

    @Override
    public @Nullable ClickConsumer skyblockapi$getOnClickAction() {
        return clickAction;
    }

    @Override
    public void skyblockapi$setBackgroundItem(@Nullable ItemStack item) {
        this.backgroundItem = item;
    }

    @Override
    public @Nullable ItemStack skyblockapi$getBackgroundItem() {
        return this.backgroundItem;
    }

    @Override
    public void skyblockapi$setBackgroundColor(int color) {
        this.backgroundColor = color;
    }

    @Override
    public int skyblockapi$getBackgroundColor() {
        return this.backgroundColor;
    }

    @Override
    public void skyblockapi$setForegroundColor(int color) {
        this.foregroundColor = color;
    }

    @Override
    public int skyblockapi$getForegroundColor() {
        return this.foregroundColor;
    }

    @Override
    public void skyblockapi$setBorderColor(int color) {
        this.borderColor = color;
    }

    @Override
    public int skyblockapi$getBorderColor() {
        return this.borderColor;
    }
}
