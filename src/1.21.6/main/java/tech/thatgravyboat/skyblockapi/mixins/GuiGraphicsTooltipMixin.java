package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.minecraft.ui.GatherItemTooltipComponentsEvent;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderItemBarEvent;

import java.util.ArrayList;
import java.util.List;

@Mixin(GuiGraphics.class)
public class GuiGraphicsTooltipMixin {

    @Unique
    private ThreadLocal<ItemStack> lastStack = ThreadLocal.withInitial(() -> ItemStack.EMPTY);

    @WrapOperation(method = "renderItemBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isBarVisible()Z"))
    private boolean itemBarVisible(ItemStack instance, Operation<Boolean> original, @Share("bar") LocalRef<RenderItemBarEvent> bar) {
        var event = new RenderItemBarEvent(instance, 0, -1f);
        event.post(SkyBlockAPI.getEventBus());
        bar.set(event);
        return (event.getPercent() >= 0f && event.getColor() != 0) || original.call(instance);
    }

    @WrapOperation(method = "renderItemBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getBarWidth()I"))
    private int itemBarWidth(ItemStack instance, Operation<Integer> original, @Share("bar") LocalRef<RenderItemBarEvent> bar) {
        if (bar.get() != null) {
            return (int) (Mth.clamp(bar.get().getPercent() * 13, 0, 13));
        }
        return original.call(instance);
    }

    @WrapOperation(method = "renderItemBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getBarColor()I"))
    private int itemBarColor(ItemStack instance, Operation<Integer> original, @Share("bar") LocalRef<RenderItemBarEvent> bar) {
        if (bar.get() != null) {
            return bar.get().getColor();
        }
        return original.call(instance);
    }

    @Inject(method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
    private void onRenderTooltipHead(Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
        lastStack.set(stack);
    }

    @WrapOperation(
        method = "setTooltipForNextFrame(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;setTooltipForNextFrameInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/ResourceLocation;Z)V"
        )
    )
    private void onRenderTooltipInternal(
        GuiGraphics instance,
        Font font,
        List<ClientTooltipComponent> list,
        int x, int y,
        ClientTooltipPositioner positioner,
        ResourceLocation texture,
        boolean force,
        Operation<Void> operation
    ) {
        List<ClientTooltipComponent> listCopy = new ArrayList<>(list);
        GatherItemTooltipComponentsEvent event = new GatherItemTooltipComponentsEvent(lastStack.get(), listCopy);
        event.post(SkyBlockAPI.getEventBus());
        operation.call(instance, font, listCopy, x, y, positioner, texture, force);
    }
}
