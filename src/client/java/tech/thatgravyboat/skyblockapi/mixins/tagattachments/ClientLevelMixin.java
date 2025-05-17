package tech.thatgravyboat.skyblockapi.mixins.tagattachments;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityAddedEvent;
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityRemovedEvent;
import tech.thatgravyboat.skyblockapi.api.events.entity.ListenForNameChange;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @Inject(method = "addEntity", at = @At("HEAD"))
    public void addEntity(Entity entity, CallbackInfo ci) {
        if (entity instanceof ArmorStand && entity instanceof ListenForNameChange nameChange) {
            nameChange.skyblockapi$markAsNameTag();
        }
    }

    @Inject(method = "addEntity", at = @At(value = "TAIL"))
    public void addEntityAfter(Entity entity, CallbackInfo ci) {
        new EntityAddedEvent(entity).post(SkyBlockAPI.getEventBus());
    }

    @WrapOperation(method = "removeEntity", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;onClientRemoval()V"))
    public void removeEntityAfter(Entity instance, Operation<Void> original) {
        new EntityRemovedEvent(instance).post(SkyBlockAPI.getEventBus());
        original.call(instance);
    }

}
