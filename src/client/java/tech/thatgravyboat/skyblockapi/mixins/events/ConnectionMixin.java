package tech.thatgravyboat.skyblockapi.mixins.events;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent;

@Mixin(Connection.class)
public class ConnectionMixin {

    @Inject(method = "genericsFtw", at = @At("HEAD"))
    private static void genericsFtw(Packet<?> packet, PacketListener packetListener, CallbackInfo ci) {
        new PacketReceivedEvent(packet).post(SkyBlockAPI.getEventBus());
    }
}
