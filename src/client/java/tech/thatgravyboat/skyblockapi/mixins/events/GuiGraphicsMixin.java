package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipPositioner;
import net.minecraft.resources.ResourceLocation;
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
public class GuiGraphicsMixin {

    @Unique
    private ThreadLocal<ItemStack> lastStack = ThreadLocal.withInitial(() -> ItemStack.EMPTY);

    @WrapOperation(method = "renderItemBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isBarVisible()Z"))
    private boolean itemBarVisible(ItemStack instance, Operation<Boolean> original, @Share("bar") LocalRef<RenderItemBarEvent> bar) {
        var event = new RenderItemBarEvent(instance, -1, 0f);
        event.post(SkyBlockAPI.getEventBus());
        bar.set(event);
        return event.getPercent() > 0f || original.call(instance);
    }

    @WrapOperation(method = "renderItemBar", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getBarWidth()I"))
    private int itemBarWidth(ItemStack instance, Operation<Integer> original, @Share("bar") LocalRef<RenderItemBarEvent> bar) {
        if (bar.get() != null) {
            return (int) (bar.get().getPercent() * 13);
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

    @Inject(method = "renderTooltip(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;II)V", at = @At("HEAD"))
    private void onRenderTooltipHead(net.minecraft.client.gui.Font font, ItemStack stack, int x, int y, CallbackInfo ci) {
        lastStack.set(stack);
    }

    @WrapOperation(
        method = "renderTooltip(Lnet/minecraft/client/gui/Font;Ljava/util/List;Ljava/util/Optional;IILnet/minecraft/resources/ResourceLocation;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;renderTooltipInternal(Lnet/minecraft/client/gui/Font;Ljava/util/List;IILnet/minecraft/client/gui/screens/inventory/tooltip/ClientTooltipPositioner;Lnet/minecraft/resources/ResourceLocation;)V"
        )
    )
    private void onRenderTooltipInternal(
        GuiGraphics instance,
        Font font,
        List<ClientTooltipComponent> list,
        int i,
        int j,
        ClientTooltipPositioner positioner,
        ResourceLocation texture,
        Operation<Void> operation
    ) {
        List<ClientTooltipComponent> listCopy = new ArrayList<>(list);
        GatherItemTooltipComponentsEvent event = new GatherItemTooltipComponentsEvent(lastStack.get(), listCopy);
        event.post(SkyBlockAPI.getEventBus());
        operation.call(instance, font, listCopy, i, j, positioner, texture);
    }
}
