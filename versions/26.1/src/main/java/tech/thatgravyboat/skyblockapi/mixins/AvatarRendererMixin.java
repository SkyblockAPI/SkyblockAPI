package tech.thatgravyboat.skyblockapi.mixins;

import net.minecraft.client.renderer.entity.player.AvatarRenderer;
import net.minecraft.client.renderer.entity.state.AvatarRenderState;
import net.minecraft.world.entity.Avatar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI;
import tech.thatgravyboat.skyblockapi.api.events.render.AvatarRenderEvent;

@Mixin(AvatarRenderer.class)
public class AvatarRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Avatar;Lnet/minecraft/client/renderer/entity/state/AvatarRenderState;F)V", at = @At("TAIL"))
    public <AvatarlikeEntity extends Avatar, S extends AvatarRenderState> void extractRenderState(
        AvatarlikeEntity entity,
        S entityRenderState,
        float f,
        CallbackInfo ci
    ) {
        var event = AvatarRenderEvent.INSTANCE;
        event.setEntity(entity);
        event.setState(entityRenderState);
        event.post(SkyBlockAPI.getEventBus());
        event.clear();
    }


}
