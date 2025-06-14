package tech.thatgravyboat.skyblockapi.mixins.events;

import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.entity.state.PlayerRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.events.render.PlayerRenderEvent;

@Mixin(PlayerRenderer.class)
public class PlayerRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/client/player/AbstractClientPlayer;Lnet/minecraft/client/renderer/entity/state/PlayerRenderState;F)V", at = @At("TAIL"))
    public <E extends AbstractClientPlayer, S extends PlayerRenderState> void extractRenderState(
        E entity,
        S entityRenderState,
        float f,
        CallbackInfo ci
    ) {
        var event = PlayerRenderEvent.INSTANCE;
        event.setEntity$skyblock_api_client(entity);
        event.setState$skyblock_api_client(entityRenderState);
        event.post$skyblock_api_client();
        event.clear();
    }


}
