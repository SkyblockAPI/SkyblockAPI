package tech.thatgravyboat.skyblockapi.mixins.features;

import net.minecraft.network.chat.ClickEvent;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tech.thatgravyboat.skyblockapi.hooks.RunnableClickEventHook;

@Mixin(ClickEvent.SuggestCommand.class)
public class ClickEventMixin implements RunnableClickEventHook {

    @Unique
    private Runnable skyblockapi$runnable = null;

    @Override
    public void skyblockapi$setRunnable(@Nullable Runnable action) {
        this.skyblockapi$runnable = action;
    }

    @Override
    public @Nullable Runnable skyblockapi$getRunnable() {
        return this.skyblockapi$runnable;
    }
}
