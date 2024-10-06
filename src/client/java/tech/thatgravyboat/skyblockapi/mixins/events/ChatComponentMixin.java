package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatComponentExtension;
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatIdHolder;

import java.util.List;
import java.util.Objects;

@Mixin(ChatComponent.class)
public abstract class ChatComponentMixin implements ChatComponentExtension {

    @Unique
    private String skyblockapi$idToGive = null;

    @Shadow
    @Final
    private List<GuiMessage> allMessages;

    @Shadow
    protected abstract void refreshTrimmedMessages();

    @Override
    public void skyblockapi$setIdForMessage(@Nullable String id) {
        this.skyblockapi$idToGive = id;
    }

    @WrapOperation(
        method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)V",
        at = @At(value = "NEW", target = "(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/GuiMessageTag;)Lnet/minecraft/client/GuiMessage;")
    )
    private GuiMessage onAddMessage(int i, Component component, MessageSignature messageSignature, GuiMessageTag guiMessageTag, Operation<GuiMessage> original) {
        GuiMessage message = original.call(i, component, messageSignature, guiMessageTag);
        if (this.skyblockapi$idToGive != null) {
            ((ChatIdHolder) (Object) message).skyblockapi$setId(this.skyblockapi$idToGive);

            this.allMessages.removeIf(m -> {
                var msgId = ((ChatIdHolder) (Object) m).skyblockapi$getId();
                return msgId != null && this.skyblockapi$idToGive != null && Objects.equals(msgId, this.skyblockapi$idToGive);
            });
            this.refreshTrimmedMessages();
        }
        return message;
    }
}
