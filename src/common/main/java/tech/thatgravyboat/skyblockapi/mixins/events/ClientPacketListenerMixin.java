package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundUpdateAttributesPacket;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityAttributesUpdateEvent;
import tech.thatgravyboat.skyblockapi.api.events.entity.EntityEquipmentUpdateEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleSetEquipment", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void handleSetEquipment(CallbackInfo ci, @Local LivingEntity entity) {
        new EntityEquipmentUpdateEvent(entity).post(SkyBlockAPI.getEventBus());
    }

    @Inject(method = "handleUpdateAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeMap;"))
    private void startHandlingAttributes(ClientboundUpdateAttributesPacket packet, CallbackInfo ci, @Share("modifiedAttributes") LocalRef<Map<Attribute, Double>> modifiedAttributesRef) {
        modifiedAttributesRef.set(new HashMap<>());
    }

    @WrapOperation(method = "handleUpdateAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;setBaseValue(D)V"))
    private void addModifiedAttribute(AttributeInstance instance, double baseValue, Operation<Void> original, @Share("modifiedAttributes") LocalRef<Map<Attribute, Double>> modifiedAttributesRef) {
        var prevValue = instance.getBaseValue();
        if (prevValue != baseValue) modifiedAttributesRef.get().put(instance.getAttribute().value(), prevValue);
        original.call(instance, baseValue);
    }

    @WrapOperation(method = "handleUpdateAttributes", at = @At(value = "INVOKE", target = "Ljava/util/Iterator;hasNext()Z"))
    private boolean postAttributesUpdate(Iterator<ClientboundUpdateAttributesPacket.AttributeSnapshot> instance, Operation<Boolean> original, @Local Entity entity, @Share("modifiedAttributes") LocalRef<Map<Attribute, Double>> modifiedAttributesRef) {
        if (original.call(instance)) return true;
        var modified = modifiedAttributesRef.get();
        if (!modified.isEmpty()) new EntityAttributesUpdateEvent((LivingEntity) entity, modified).post(SkyBlockAPI.getEventBus());
        return false;
    }

}
