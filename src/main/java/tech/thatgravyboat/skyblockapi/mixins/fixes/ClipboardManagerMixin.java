package tech.thatgravyboat.skyblockapi.mixins.fixes;

//? if >= 26.3 {
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.platform.ClipboardManager;
import org.lwjgl.sdl.SDLClipboard;
import org.lwjgl.system.MemoryUtil;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

@Mixin(ClipboardManager.class)
public class ClipboardManagerMixin {

    // Texts bigger than the stack (by default 64KB) cannot get directly copied because they throw an OOME
    @WrapOperation(method = "setClipboard", at = @At(value = "INVOKE", target = "Lorg/lwjgl/sdl/SDLClipboard;SDL_SetClipboardText(Ljava/lang/CharSequence;)Z"))
    private boolean skyblockapi$fixLargeClipboardText(CharSequence text, Operation<Boolean> original) {
        try {
            return original.call(text);
        } catch (OutOfMemoryError ignored) {
            var encoded = ((String) text).getBytes(StandardCharsets.UTF_8);

            var success = false;
            ByteBuffer buffer = MemoryUtil.memAlloc(encoded.length + 1);
            try {
                buffer.put(encoded).put((byte) 0).flip();
                success = SDLClipboard.SDL_SetClipboardText(buffer);
            } finally {
                MemoryUtil.memFree(buffer);
            }
            return success;
        }
    }

}
//?}
