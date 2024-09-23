package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionbarReceivedEvent;
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent;

@Mixin(ChatListener.class)
public class ChatListenerMixin {

    @WrapOperation(
        method = "handleSystemMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V"
        )
    )
    private void onSetOverlayMessage(Gui instance, Component component, boolean bl, Operation<Void> original) {
        ActionbarReceivedEvent event = new ActionbarReceivedEvent(component);
        if (event.post(SkyBlockAPI.getEventBus())) return;
        original.call(instance, event.getComponent(), bl);
    }

    @WrapOperation(
        method = "handleSystemMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;)V"
        )
    )
    private void onAddMessage(ChatComponent instance, Component component, Operation<Void> original) {
        ChatReceivedEvent event = new ChatReceivedEvent(component);
        if (event.post(SkyBlockAPI.getEventBus())) return;
        original.call(instance, event.getComponent());
    }
}
