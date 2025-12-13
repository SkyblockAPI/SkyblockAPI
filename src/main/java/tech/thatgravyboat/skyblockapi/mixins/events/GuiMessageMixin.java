package tech.thatgravyboat.skyblockapi.mixins.events;

import net.minecraft.client.GuiMessage;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tech.thatgravyboat.skyblockapi.impl.events.chat.ChatIdHolder;

@Mixin(GuiMessage.class)
public class GuiMessageMixin implements ChatIdHolder {

    @Unique
    private @Nullable String skyblockapi$id;

    @Override
    public @Nullable String skyblockapi$getId() {
        return this.skyblockapi$id;
    }

    @Override
    public void skyblockapi$setId(@Nullable String id) {
        this.skyblockapi$id = id;
    }
}
