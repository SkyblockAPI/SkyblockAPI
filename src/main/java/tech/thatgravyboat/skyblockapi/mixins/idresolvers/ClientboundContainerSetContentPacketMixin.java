package tech.thatgravyboat.skyblockapi.mixins.idresolvers;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId;
import tech.thatgravyboat.skyblockapi.api.remote.api.resolvers.IdResolverKind;

@Mixin(ClientboundContainerSetContentPacket.class)
public class ClientboundContainerSetContentPacketMixin {


    @ModifyExpressionValue(method = "<clinit>", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/codec/StreamCodec;composite(Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lnet/minecraft/network/codec/StreamCodec;Ljava/util/function/Function;Lcom/mojang/datafixers/util/Function4;)Lnet/minecraft/network/codec/StreamCodec;"))
    private static <B, C> StreamCodec<B, C> wrap(StreamCodec<B, C> original) {
        return SkyBlockId.wrap(original, IdResolverKind.ContainerContents);
    }

}
