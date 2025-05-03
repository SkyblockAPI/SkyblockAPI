package tech.thatgravyboat.skyblockapi.mixins.events;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.level.PacketSentEvent;

@Mixin(ClientCommonPacketListenerImpl.class)
public class ClientCommonPacketListenerImplMixin {

    @Inject(method = "send", at = @At("HEAD"))
    private void send(Packet<?> packet, CallbackInfo ci) {
        new PacketSentEvent(packet).post(SkyBlockAPI.getEventBus());
    }
}
