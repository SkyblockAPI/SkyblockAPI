package tech.thatgravyboat.skyblockapi.mixins.tagattachments;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.DebugScreenOverlay;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityEvents;
import tech.thatgravyboat.skyblockapi.helpers.SkyBlockEntity;

import java.util.List;

@Mixin(DebugScreenOverlay.class)
public class DebugScreenOverlayMixin {

    @Inject(method = "getSystemInformation", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;getType()Lnet/minecraft/world/entity/EntityType;"))
    public void getSystemInfo(CallbackInfoReturnable<List<String>> cir, @Local List<String> list, @Local Entity entity) {
        if (!EntityEvents.INSTANCE.getDebug()) {
            return;
        }
        list.add(String.valueOf(SkyBlockEntity.getAttachedLines(entity).size()));
        for (Component attachedLine : SkyBlockEntity.getAttachedLines(entity)) {
            list.add(attachedLine.getString());
        }
    }

}
