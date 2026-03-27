package tech.thatgravyboat.skyblockapi.mixins.idresolvers;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundSetEquipmentPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId;
import tech.thatgravyboat.skyblockapi.api.remote.api.resolvers.IdResolverKind;

@Mixin(ClientboundSetEquipmentPacket.class)
public class ClientboundSetEquipmentPacketMixin {

    @WrapOperation(method = "<init>(Lnet/minecraft/network/RegistryFriendlyByteBuf;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;decode(Ljava/lang/Object;)Ljava/lang/Object;"))
    private Object wrap(StreamCodec<?, ?> instance, Object o, Operation<Object> original) {
        SkyBlockId.Companion.setIdResolverKind(IdResolverKind.Equipment);
        var result = original.call(instance, o);
        SkyBlockId.Companion.setIdResolverKind(IdResolverKind.Unknown);
        return result;
    }

}
