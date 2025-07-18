package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.screen.ItemTooltipEvent;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public class ItemStackMixin {

    @Unique
    private final ThreadLocal<List<Component>> list = new ThreadLocal<>();

    @Inject(method = "getTooltipLines", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/world/item/ItemStack;addDetailsToTooltip(Lnet/minecraft/world/item/Item$TooltipContext;Lnet/minecraft/world/item/component/TooltipDisplay;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/TooltipFlag;Ljava/util/function/Consumer;)V"
    ))
    private void getTooltipLines(Item.TooltipContext tooltipContext, Player player, TooltipFlag tooltipFlag, CallbackInfoReturnable<List<Component>> cir, @Local List<Component> list) {
        this.list.set(list);
    }

    @Inject(method = "addDetailsToTooltip", at = @At(
        value = "INVOKE",
        target = "Lnet/minecraft/world/item/TooltipFlag;isAdvanced()Z",
        shift = At.Shift.AFTER,
        ordinal = 0
    ))
    private void getTooltipLines(Item.TooltipContext tooltipContext, TooltipDisplay tooltipDisplay, Player player, TooltipFlag tooltipFlag, Consumer<Component> consumer, CallbackInfo ci) {
        if (this.list.get() == null) return; // Vanilla calls this method themselves when getting a crossbow tooltip without calling the normal item tooltip, we don't want to post an event in that case.

        ItemStack stack = (ItemStack) (Object) this;
        new ItemTooltipEvent(stack, this.list.get()).post(SkyBlockAPI.getEventBus());
        this.list.remove();
    }
}
