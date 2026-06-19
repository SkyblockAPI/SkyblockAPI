package tech.thatgravyboat.skyblockapi.mixins;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.contextualbar.ContextualBar;
import net.minecraft.client.gui.contextualbar.ExperienceBar;
import net.minecraft.client.gui.contextualbar.JumpableVehicleBar;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.render.HudElement;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudElementEvent;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderHudEvent;

@Mixin(Hud.class)
public abstract class HudMixin {

    @Shadow
    private Pair<Enum<?>, ContextualBar> contextualInfoBar;

    @Shadow
    public abstract boolean isHidden();

    @Inject(method = "extractSleepOverlay", at = @At("HEAD"))
    private void onRenderSleepOverlay(GuiGraphicsExtractor graphics, DeltaTracker delta, CallbackInfo ci) {
        if (this.isHidden()) {
            return;
        }
        float partialTicks = delta.getGameTimeDeltaPartialTick(false);
        new RenderHudEvent(graphics, partialTicks).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(
        method = "extractHotbarAndDecorations",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Hud;extractItemHotbar(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"
        )
    )
    private boolean onRenderHotbar(Hud instance, GuiGraphicsExtractor graphics, DeltaTracker delta) {
        return !new RenderHudElementEvent(HudElement.HOTBAR, graphics).post(SkyBlockAPI.getEventBus());
    }

    @Inject(
        method = "extractHotbarAndDecorations",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractBackground(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/DeltaTracker;)V"
        )
    )
    private void onRenderBar(GuiGraphicsExtractor graphics, DeltaTracker $$1, CallbackInfo ci) {
        var renderer = this.contextualInfoBar.getSecond();
        if (renderer instanceof ExperienceBar) {
            if (new RenderHudElementEvent(HudElement.EXPERIENCE, graphics).post(SkyBlockAPI.getEventBus())) {
                this.contextualInfoBar = Pair.of(this.contextualInfoBar.getFirst(), ContextualBar.EMPTY);
            }
        } else if (renderer instanceof JumpableVehicleBar) {
            if (new RenderHudElementEvent(HudElement.JUMP, graphics).post(SkyBlockAPI.getEventBus())) {
                this.contextualInfoBar = Pair.of(this.contextualInfoBar.getFirst(), ContextualBar.EMPTY);
            }
        }
    }

    @WrapWithCondition(
        method = "extractHotbarAndDecorations",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/contextualbar/ContextualBar;extractExperienceLevel(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/client/gui/Font;I)V"
        )
    )
    private boolean onRenderExperienceLevel(GuiGraphicsExtractor $$0, Font $$1, int $$2) {
        return this.contextualInfoBar.getFirst().ordinal() != 1 || this.contextualInfoBar.getSecond() != ContextualBar.EMPTY;
    }

    @WrapWithCondition(
        method = "extractHotbarAndDecorations",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Hud;extractVehicleHealth(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"
        )
    )
    private boolean onRenderVehicleHealth(Hud instance, GuiGraphicsExtractor graphics) {
        return !new RenderHudElementEvent(HudElement.HEALTH, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(
        method = "extractPlayerHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Hud;extractHearts(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;IIIIFIIIZ)V"
        )
    )
    private boolean onRenderHealth(
        Hud instance,
        GuiGraphicsExtractor graphics,
        Player player,
        int i,
        int j,
        int k,
        int l,
        float f,
        int m,
        int n,
        int o,
        boolean bl
    ) {
        return !new RenderHudElementEvent(HudElement.HEALTH, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(
        method = "extractPlayerHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Hud;extractArmor(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;IIII)V"
        )
    )
    private boolean onRenderArmor(GuiGraphicsExtractor graphics, Player player, int i, int j, int k, int l) {
        return !new RenderHudElementEvent(HudElement.ARMOR, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(
        method = "extractPlayerHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Hud;extractAirBubbles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;III)V"
        )
    )
    private boolean onRenderFood(Hud instance, GuiGraphicsExtractor graphics, Player player, int vehicleHearts, int yLineAir, int xRight) {
        return !new RenderHudElementEvent(HudElement.FOOD, graphics).post(SkyBlockAPI.getEventBus());
    }

    @WrapWithCondition(
        method = "extractPlayerHealth",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Hud;extractAirBubbles(Lnet/minecraft/client/gui/GuiGraphicsExtractor;Lnet/minecraft/world/entity/player/Player;III)V"
        )
    )
    private boolean onRenderAir(Hud instance, GuiGraphicsExtractor graphics, Player player, int i, int j, int k) {
        return !new RenderHudElementEvent(HudElement.AIR, graphics).post(SkyBlockAPI.getEventBus());
    }


    @Inject(method = "extractChat", at = @At("HEAD"), cancellable = true)
    private void onChatRender(GuiGraphicsExtractor GuiGraphicsExtractor, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (new RenderHudElementEvent(HudElement.CHAT, GuiGraphicsExtractor).post(SkyBlockAPI.getEventBus())) {
            ci.cancel();
        }
    }

    @Inject(method = "displayScoreboardSidebar", at = @At("HEAD"), cancellable = true)
    private void onScoreboardRender(GuiGraphicsExtractor graphics, Objective objective, CallbackInfo ci) {
        if (new RenderHudElementEvent(HudElement.SCOREBOARD, graphics).post(SkyBlockAPI.getEventBus())) {
            ci.cancel();
        }
    }


    @Inject(method = "extractEffects", at = @At("HEAD"), cancellable = true)
    private void onEffectsRender(GuiGraphicsExtractor graphics, DeltaTracker deltaTracker, CallbackInfo ci) {
        if (new RenderHudElementEvent(HudElement.EFFECTS, graphics).post(SkyBlockAPI.getEventBus())) {
            ci.cancel();
        }
    }

}
