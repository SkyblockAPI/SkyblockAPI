package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.*;
import net.minecraft.network.protocol.game.GamePacketTypes;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.level.PacketReceivedEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Mixin(BundlerInfo.class)
public interface BundlerInfoMixin {

    @WrapMethod(method = "createForPacket")
    private static <T extends PacketListener, P extends BundlePacket<? super T>> BundlerInfo createForPacketWrapMethod(
        final PacketType<@NotNull P> type,
        final Function<Iterable<Packet<? super T>>, P> bundler,
        final BundleDelimiterPacket<? super T> delimiter,
        final Operation<BundlerInfo> original
    ) {
        // Sanity check, we only want to modify clientbound bundle packets even though this method is only called for them
        if (type == GamePacketTypes.CLIENTBOUND_BUNDLE) {
            return original.call(type, (Function<Iterable<Packet<? super T>>, P>) (iterable) -> {
                // The default capacity should be enough, if hypixel ever starts sending more than 10 packets in a bundle we can revisit this
                List<Packet<? super T>> packets = new ArrayList<>();
                for (var packet : iterable) {
                    if (!new PacketReceivedEvent(packet).post(SkyBlockAPI.getEventBus())) {
                        packets.add(packet);
                    }
                }

                return bundler.apply(packets);
            }, delimiter);
        }
        return original.call(type, bundler, delimiter);
    }

}
