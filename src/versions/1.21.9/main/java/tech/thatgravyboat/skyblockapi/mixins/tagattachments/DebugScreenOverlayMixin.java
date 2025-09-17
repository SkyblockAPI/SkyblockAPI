package tech.thatgravyboat.skyblockapi.mixins.tagattachments;

import net.minecraft.client.gui.components.debug.DebugScreenEntries;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.TagAttachmentDebugEntry;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;

@Mixin(DebugScreenEntries.class)
public abstract class DebugScreenOverlayMixin {

    @Inject(method = "<clinit>", at = @At(value = "TAIL"))
    private static void getSystemInfo(CallbackInfo ci) {
        DebugScreenEntries.register(
            ResourceLocation.fromNamespaceAndPath(SkyBlockAPI.INSTANCE.getMOD_ID(), "tag_attachments"),
            TagAttachmentDebugEntry.INSTANCE);
    }

}
