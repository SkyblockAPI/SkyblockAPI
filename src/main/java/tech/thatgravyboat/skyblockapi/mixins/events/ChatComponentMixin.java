package tech.thatgravyboat.skyblockapi.mixins.events;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.gui.components.ChatComponent;
import net.minecraft.client.multiplayer.chat.GuiMessage;
//? >= 26.1
import net.minecraft.client.multiplayer.chat.GuiMessageSource;
import net.minecraft.client.multiplayer.chat.GuiMessageTag;
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
            //? >= 26.1 {
            @At(
                value = "NEW",
                target = "(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageSource;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)Lnet/minecraft/client/multiplayer/chat/GuiMessage;"
            ),
            //? } else {
            /*@At(
                value = "NEW",
                target = "(ILnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)Lnet/minecraft/client/multiplayer/chat/GuiMessage;"
            )
            *///? }
        }
    )
    private GuiMessage onAddMessage(
        int addedTime,
        Component content,
        MessageSignature signature,
        //? >= 26.1
        GuiMessageSource source,
        GuiMessageTag tag,
        Operation<GuiMessage> original
    ) {
        GuiMessage message = original.call(
            addedTime,
            content,
            signature,
            //? >= 26.1
            source,
            tag
        );
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
            //? >= 26.1 {
            target = "(Lnet/minecraft/client/multiplayer/chat/GuiMessage;Lnet/minecraft/util/FormattedCharSequence;Z)Lnet/minecraft/client/multiplayer/chat/GuiMessage$Line;"
            //? } else {
            /*target = "(ILnet/minecraft/util/FormattedCharSequence;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;Z)Lnet/minecraft/client/multiplayer/chat/GuiMessage$Line;"
            *///? }
        )
    )
    private GuiMessage.Line onAddMessageToDisplayQueue(
        //? >= 26.1
        GuiMessage parent,
        FormattedCharSequence content,
        //? < 26.1
        //GuiMessageTag tag,
        boolean endOfEntry,
        Operation<GuiMessage.Line> original,
        @Local(argsOnly = true) GuiMessage message
    ) {
        GuiMessage.Line line = original.call(
            //? >= 26.1
            parent,
            content,
            //? < 26.1
            //tag,
            endOfEntry
        );
        ChatIdHolder messageHolder = (ChatIdHolder) (Object) message;
        if (messageHolder != null && messageHolder.skyblockapi$getId() != null) {
            ((ChatIdHolder) (Object) line).skyblockapi$setId(messageHolder.skyblockapi$getId());
        }
        return line;
    }
}
