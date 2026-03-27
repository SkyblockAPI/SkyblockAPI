package tech.thatgravyboat.skyblockapi.mixins.idresolvers;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundSetPlayerInventoryPacket;
import org.jspecify.annotations.NonNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId;
import tech.thatgravyboat.skyblockapi.api.remote.api.resolvers.IdResolverKind;

@Mixin(ClientboundSetPlayerInventoryPacket.class)
public class ClientboundSetPlayerInventoryPacketMixin {
    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Ljava/util/function/BiFunction;)Lnet/minecraft/network/codec/StreamCodec;"))
    private static <B, C> StreamCodec<B, C> wrap(StreamCodec<B, C> original) {
        return new StreamCodec<>() {
            @Override
            public @NonNull C decode(@NonNull B object) {
                SkyBlockId.Companion.setIdResolverKind(IdResolverKind.Inventory);
                var result = original.decode(object);
                SkyBlockId.Companion.setIdResolverKind(IdResolverKind.Unknown);
                return result;
            }

            @Override
            public void encode(@NonNull B object, @NonNull C object2) {
                original.encode(object, object2);
            }
        };
    }
}
