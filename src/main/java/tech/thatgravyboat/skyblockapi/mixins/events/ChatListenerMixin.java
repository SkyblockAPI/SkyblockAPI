package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.helpers.McClient;
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatComponentExtension;

@Mixin(ChatListener.class)
public class ChatListenerMixin {

    @WrapOperation(
        method = "handleSystemMessage",
        at = @At(
            value = "INVOKE",
            //~ if >= 26.1 'addMessage' -> 'addClientSystemMessage'
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addClientSystemMessage(Lnet/minecraft/network/chat/Component;)V"
        )
    )
    private void onAddMessage(ChatComponent instance, Component component, Operation<Void> original) {
        original.call(instance, component);
        ((ChatComponentExtension) McClient.INSTANCE.getChat()).skyblockapi$setIdForMessage(null);
    }
}
