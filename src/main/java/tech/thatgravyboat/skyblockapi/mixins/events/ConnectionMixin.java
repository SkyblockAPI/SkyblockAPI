package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import org.spongepowered.asm.mixin.Mixin;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent;

@Mixin(Connection.class)
public class ConnectionMixin {

    @WrapMethod(method = "genericsFtw")
    private static void genericsFtw(Packet<?> packet, PacketListener listener, Operation<Void> original) {
        // The bundle packets are special and are already handled in the BundleInfoMixin
        if (packet instanceof ClientboundBundlePacket || !new PacketReceivedEvent(packet).post(SkyBlockAPI.getEventBus())) {
            original.call(packet, listener);
        }
    }
}
