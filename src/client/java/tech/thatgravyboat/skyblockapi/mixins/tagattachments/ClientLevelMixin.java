package tech.thatgravyboat.skyblockapi.mixins.tagattachments;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.events.entity.ListenForNameChange;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(method = "addEntity", at = @At("HEAD"))
    public void addEntity(Entity entity, CallbackInfo ci) {
        if (entity instanceof ArmorStand) {
            ((ListenForNameChange) entity).skyblockapi$markAsNameTag();
        }
    }

}
