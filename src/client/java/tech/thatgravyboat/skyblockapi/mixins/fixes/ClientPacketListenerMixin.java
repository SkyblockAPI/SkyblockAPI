package tech.thatgravyboat.skyblockapi.mixins.fixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.multiplayer.ClientPacketListener;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {

    @WrapOperation(
        method = "handlePlayerInfoUpdate",
        at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/Object;)V")
    )
    private void onPlayerInfoUpdateWarn(Logger instance, String s, Object o1, Object o2, Operation<Void> original) {
        // Do nothing
    }

    @WrapOperation(
        method = "handleSetEntityPassengersPacket",
        at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;)V")
    )
    private void onSetEntityPassengersPacketWarn(Logger instance, String s, Operation<Void> original) {
        // Do nothing
    }
}
