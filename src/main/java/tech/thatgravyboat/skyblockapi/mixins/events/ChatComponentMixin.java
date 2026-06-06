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
        //? >= 26.1 {
        method = "addMessage",
        //? } else
        //method = "addMessage(Lnet/minecraft/network/chat/Component;Lnet/minecraft/network/chat/MessageSignature;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;)V",
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

            this.allMessages.removeIf(msg -> {
                var msgId = ((ChatIdHolder) (Object) msg).skyblockapi$getId();
                return msgId != null && this.skyblockapi$idToGive != null && Objects.equals(msgId, this.skyblockapi$idToGive);
            });
            this.trimmedMessages.removeIf(line -> {
                //~ if >= 26.1 'line' -> 'line.parent()'
                var msgId = ((ChatIdHolder) (Object) line.parent()).skyblockapi$getId();
                return msgId != null && this.skyblockapi$idToGive != null && Objects.equals(msgId, this.skyblockapi$idToGive);
            });
        }
        return message;
    }

    //? if < 26.1 {
    /*@WrapOperation(
        method = "addMessageToDisplayQueue",
        at = @At(
            value = "NEW",
            target = "(ILnet/minecraft/util/FormattedCharSequence;Lnet/minecraft/client/multiplayer/chat/GuiMessageTag;Z)Lnet/minecraft/client/multiplayer/chat/GuiMessage$Line;"
        )
    )
    private GuiMessage.Line onAddMessageToDisplayQueue(
        int i,
        FormattedCharSequence content,
        GuiMessageTag tag,
        boolean endOfEntry,
        Operation<GuiMessage.Line> original,
        @Local(argsOnly = true) GuiMessage message
    ) {
        GuiMessage.Line line = original.call(
            i,
            content,
            tag,
            endOfEntry
        );
        ChatIdHolder messageHolder = (ChatIdHolder) (Object) message;
        if (messageHolder != null && messageHolder.skyblockapi$getId() != null) {
            ((ChatIdHolder) (Object) line).skyblockapi$setId(messageHolder.skyblockapi$getId());
        }
        return line;
    }*///?}
}
