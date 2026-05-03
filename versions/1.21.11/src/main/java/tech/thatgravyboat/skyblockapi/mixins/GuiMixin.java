package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.contextualbar.ContextualBarRenderer;
import net.minecraft.client.gui.contextualbar.ExperienceBarRenderer;
import net.minecraft.client.gui.contextualbar.JumpableVehicleBarRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import org.apache.commons.lang3.tuple.Pair;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudElementEvent;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent;

@Mixin(Gui.class)
public class GuiMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    private Pair<Enum<?>, ContextualBarRenderer> contextualInfoBar;

    @Inject(method = "renderSleepOverlay", at = @At("HEAD"))
    private void onRenderSleepOverlay(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        if (this.minecraft.options.hideGui) return;
        float partialTicks = delta.getGameTimeDeltaPartialTick(false);
        new RenderHudEvent(graphics, partialTicks).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private boolean onRenderHotbar(Gui instance, GuiGraphics graphics, DeltaTracker delta) {
        return !new RenderHudElementEvent(HudElement.HOTBAR, graphics).post(SkyBlockAPI.getEventBus());
    }

    @Inject(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBarRenderer;renderBackground(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private void onRenderBar(GuiGraphics graphics, DeltaTracker $$1, CallbackInfo ci) {
        var renderer = this.contextualInfoBar.getRight();
        if (renderer instanceof ExperienceBarRenderer) {
            if (new RenderHudElementEvent(HudElement.EXPERIENCE, graphics).post(SkyBlockAPI.getEventBus())) {
                this.contextualInfoBar = Pair.of(this.contextualInfoBar.getLeft(), ContextualBarRenderer.EMPTY);
            }
        } else if (renderer instanceof JumpableVehicleBarRenderer) {
            if (new RenderHudElementEvent(HudElement.JUMP, graphics).post(SkyBlockAPI.getEventBus())) {
                this.contextualInfoBar = Pair.of(this.contextualInfoBar.getLeft(), ContextualBarRenderer.EMPTY);
            }
        }
    }

    @WrapWithCondition(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/contextualbar/ContextualBarRenderer;renderExperienceLevel(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/gui/Font;I)V"))
    private boolean onRenderExperienceLevel(GuiGraphics $$0, Font $$1, int $$2) {
        return this.contextualInfoBar.getKey().ordinal() != 1 || this.contextualInfoBar.getValue() != ContextualBarRenderer.EMPTY;
    }

    @WrapWithCondition(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderVehicleHealth(Lnet/minecraft/client/gui/GuiGraphics;)V"))
    private boolean onRenderVehicleHealth(Gui instance, GuiGraphics graphics) {
        return !new RenderHudElementEvent(HudElement.HEALTH, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderHearts(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"))
    private boolean onRenderHealth(Gui instance, GuiGraphics graphics, Player player, int i, int j, int k, int l, float f, int m, int n, int o, boolean bl) {
        return !new RenderHudElementEvent(HudElement.HEALTH, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderArmor(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;IIII)V"))
    private boolean onRenderArmor(GuiGraphics graphics, Player player, int i, int j, int k, int l) {
        return !new RenderHudElementEvent(HudElement.ARMOR, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderFood(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;II)V"))
    private boolean onRenderFood(Gui instance, GuiGraphics graphics, Player player, int i, int j) {
        return !new RenderHudElementEvent(HudElement.FOOD, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderAirBubbles(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/entity/player/Player;III)V"))
    private boolean onRenderAir(Gui instance, GuiGraphics graphics, Player player, int i, int j, int k) {
        return !new RenderHudElementEvent(HudElement.AIR, graphics).post(SkyBlockAPI.getEventBus());
    }

    @Inject(method = "renderChat", at = @At("HEAD"), cancellable = true)
    private void onChatRender(GuiGraphics guiGraphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (new RenderHudElementEvent(HudElement.CHAT, guiGraphics).post(SkyBlockAPI.getEventBus())) {
            ci.cancel();
        }
    }

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void onScoreboardRender(GuiGraphics graphics, Objective objective, CallbackInfo ci) {
        if (new RenderHudElementEvent(HudElement.SCOREBOARD, graphics).post(SkyBlockAPI.getEventBus())) {
            ci.cancel();
        }
    }

    @Inject(method = "renderEffects", at = @At("HEAD"), cancellable = true)
    private void onEffectsRender(GuiGraphics graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (new RenderHudElementEvent(HudElement.EFFECTS, graphics).post(SkyBlockAPI.getEventBus())) {
            ci.cancel();
        }
    }

}
