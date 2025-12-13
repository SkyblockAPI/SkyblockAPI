package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import java.util.List;
import java.util.Objects;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.GuiMessageTag;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MessageSignature;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatComponentExtension;
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatIdHolder;

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
            this.trimmedMessages.removeIf(it -> {
                var msgId = ((ChatIdHolder) (Object) it).skyblockapi$getId();
                return msgId != null && this.skyblockapi$idToGive != null && Objects.equals(msgId, this.skyblockapi$idToGive);
            });
        }
        return message;
    }

    @WrapOperation(
        method = "addMessageToDisplayQueue",
        at = @At(
            value = "NEW",
            target = "(ILnet/minecraft/util/FormattedCharSequence;Lnet/minecraft/client/GuiMessageTag;Z)Lnet/minecraft/client/GuiMessage$Line;"
        )
    )
    private GuiMessage.Line onAddMessageToDisplayQueue(
        int time,
        FormattedCharSequence content,
        GuiMessageTag tag,
        boolean endOfEntry,
        Operation<GuiMessage.Line> original,
        @Local(argsOnly = true) GuiMessage message
    ) {
        GuiMessage.Line line = original.call(time, content, tag, endOfEntry);
        ChatIdHolder messageHolder = (ChatIdHolder) (Object) message;
        if (messageHolder != null && messageHolder.skyblockapi$getId() != null) {
            ((ChatIdHolder) (Object) line).skyblockapi$setId(messageHolder.skyblockapi$getId());
        }
        return line;
    }
}
