package tech.thatgravyboat.skyblockapi.mixins;

import net.minecraft.SharedConstants;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

// TODO: remove mixim
@Mixin(ClientIntentionPacket.class)
public abstract class MixinClientIntentionPacket {
    @ModifyVariable(
        method = "<init>(ILjava/lang/String;ILnet/minecraft/network/protocol/handshake/ClientIntent;)V",
        at = @At("HEAD"),
        argsOnly = true,
        name = "protocolVersion"
    )
    private static int modifyProtocolVersion(int protocolVersion) {
        return SharedConstants.RELEASE_NETWORK_PROTOCOL_VERSION;
    }
}
