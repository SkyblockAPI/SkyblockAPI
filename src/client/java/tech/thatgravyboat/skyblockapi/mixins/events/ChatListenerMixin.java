package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.ChatListener;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent;
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent;
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatComponentExtension;

@Mixin(ChatListener.class)
public class ChatListenerMixin {

    @Shadow
    @Final
    private Minecraft minecraft;

    @WrapOperation(
        method = "handleSystemMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Gui;setOverlayMessage(Lnet/minecraft/network/chat/Component;Z)V"
        )
    )
    private void onSetOverlayMessage(Gui instance, Component component, boolean bl, Operation<Void> original) {
        ActionBarReceivedEvent event = new ActionBarReceivedEvent(component);
        if (event.post(SkyBlockAPI.getEventBus())) {
            original.call(instance, CommonComponents.EMPTY, bl);
        } else {
            original.call(instance, event.getComponent(), bl);
        }
    }

    @WrapOperation(
        method = "handleSystemMessage",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/components/ChatComponent;addMessage(Lnet/minecraft/network/chat/Component;)V"
        )
    )
    private void onAddMessage(ChatComponent instance, Component component, Operation<Void> original) {
        ChatReceivedEvent event = new ChatReceivedEvent(component, null);
        if (event.post(SkyBlockAPI.getEventBus())) {
            SkyBlockAPI.INSTANCE
                .getLogger$skyblock_api_client()
                .info("[Cancelled] [CHAT] {}", event.getComponent().getString());
            return;
        }
        ((ChatComponentExtension) this.minecraft.gui.getChat()).skyblockapi$setIdForMessage(event.getId());
        original.call(instance, event.getComponent());
        ((ChatComponentExtension) this.minecraft.gui.getChat()).skyblockapi$setIdForMessage(null);
    }
}
