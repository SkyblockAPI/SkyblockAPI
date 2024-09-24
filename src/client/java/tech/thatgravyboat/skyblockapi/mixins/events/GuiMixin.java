package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.PlayerRideableJumping;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
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

    @Inject(method = "renderSleepOverlay", at = @At("HEAD"))
    private void onRenderSleepOverlay(GuiGraphics graphics, DeltaTracker delta, CallbackInfo ci) {
        float partialTicks = delta.getGameTimeDeltaPartialTick(false);
        new RenderHudEvent(graphics, partialTicks).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderItemHotbar(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/client/DeltaTracker;)V"))
    private boolean onRenderHotbar(Gui instance, GuiGraphics graphics, DeltaTracker delta) {
        return !new RenderHudElementEvent(HudElement.HOTBAR, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderJumpMeter(Lnet/minecraft/world/entity/PlayerRideableJumping;Lnet/minecraft/client/gui/GuiGraphics;I)V"))
    private boolean onRenderJumpBar(Gui instance, PlayerRideableJumping playerRideableJumping, GuiGraphics graphics, int i, @Local(argsOnly = true) DeltaTracker delta) {
        return !new RenderHudElementEvent(HudElement.JUMP, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(method = "renderHotbarAndDecorations", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/Gui;renderExperienceBar(Lnet/minecraft/client/gui/GuiGraphics;I)V"))
    private boolean onRenderExperienceBar(Gui instance, GuiGraphics graphics, int i) {
        return !new RenderHudElementEvent(HudElement.EXPERIENCE, graphics).post(SkyBlockAPI.getEventBus());
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

    @Inject(method = "renderPlayerHealth", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"), cancellable = true)
    private void onAirRender(GuiGraphics guiGraphics, CallbackInfo ci) {
        if (new RenderHudElementEvent(HudElement.AIR, guiGraphics).post(SkyBlockAPI.getEventBus())) {
            this.minecraft.getProfiler().pop();
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
