package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Holder;
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
import java.util.Map;

@SuppressWarnings({"MixinAnnotationTarget", "InvalidInjectorMethodSignature"})
@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @Inject(method = "handleSetEquipment", at = @At(value = "INVOKE", target = "Ljava/util/List;forEach(Ljava/util/function/Consumer;)V", shift = At.Shift.AFTER))
    private void handleSetEquipment(CallbackInfo ci, @Local LivingEntity entity) {
        new EntityEquipmentUpdateEvent(entity).post(SkyBlockAPI.getEventBus());
    }

    @Inject(method = "handleUpdateAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getAttributes()Lnet/minecraft/world/entity/ai/attributes/AttributeMap;"))
    private void startHandlingAttributes(
        ClientboundUpdateAttributesPacket packet,
        CallbackInfo ci,
        @Share("modifiedAttributes") LocalRef<Map<Holder<Attribute>, EntityAttributesUpdateEvent.ChangedAttribute>> modifiedAttributesRef
    ) {
        modifiedAttributesRef.set(new HashMap<>());
    }

    @WrapOperation(method = "handleUpdateAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/ai/attributes/AttributeInstance;setBaseValue(D)V"))
    private void addModifiedAttribute(
        AttributeInstance instance,
        double baseValue,
        Operation<Void> original,
        @Share("modifiedAttributes") LocalRef<Map<Holder<Attribute>, EntityAttributesUpdateEvent.ChangedAttribute>> modifiedAttributesRef
    ) {
        var prevValue = instance.getBaseValue();
        if (prevValue != baseValue) {
            modifiedAttributesRef.get().put(instance.getAttribute(), new EntityAttributesUpdateEvent.ChangedAttribute(prevValue, baseValue));
        }
        original.call(instance, baseValue);
    }

    @Inject(method = "handleUpdateAttributes", at = @At("TAIL"))
    private void postAttributesUpdate(
        CallbackInfo ci,
        @Local Entity entity,
        @Share("modifiedAttributes") LocalRef<Map<Holder<Attribute>, EntityAttributesUpdateEvent.ChangedAttribute>> modifiedAttributesRef
    ) {
        var modified = modifiedAttributesRef.get();
        if (modified != null && !modified.isEmpty()) {
            new EntityAttributesUpdateEvent((LivingEntity) entity, modified).post(SkyBlockAPI.getEventBus());
        }
    }

}
