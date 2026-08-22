package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
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
    @Final
    private List<GuiMessage.Line> trimmedMessages;

    @Override
    public void skyblockapi$setIdForMessage(@Nullable String id) {
        this.skyblockapi$idToGive = id;
    }

    @WrapOperation(
        method = "addMessage",
        at = {
            @At(
                value = "NEW",
                target = "(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)Lnet/minecraft/client/multiplayer/chat/GuiMessage;"
            ),
        }
    )
    private GuiMessage onAddMessage(
        int addedTime,
        Component content,
        MessageSignature signature,
        GuiMessageSource source,
        GuiMessageTag tag,
        Operation<GuiMessage> original
    ) {
        GuiMessage message = original.call(
            addedTime,
            content,
            signature,
            source,
            tag
        );
        if (this.skyblockapi$idToGive != null) {
            ((ChatIdHolder) (Object) message).skyblockapi$setId(this.skyblockapi$idToGive);

            this.allMessages.removeIf(msg -> {
                var msgId = ((ChatIdHolder) (Object) msg).skyblockapi$getId();
                return msgId != null && this.skyblockapi$idToGive != null && Objects.equals(msgId, this.skyblockapi$idToGive);
            });
            this.trimmedMessages.removeIf(line -> {
                var msgId = ((ChatIdHolder) (Object) line.parent()).skyblockapi$getId();
                return msgId != null && this.skyblockapi$idToGive != null && Objects.equals(msgId, this.skyblockapi$idToGive);
            });
        }
        return message;
    }
}
