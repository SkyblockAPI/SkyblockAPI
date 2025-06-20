package tech.thatgravyboat.skyblockapi.mixins.events;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.events.render.RenderEntityEvent;

@Mixin(EntityRenderer.class)
public class EntityRendererMixin {

    @Inject(method = "extractRenderState", at = @At("TAIL"))
    public <E extends Entity, S extends EntityRenderState> void extractRenderState(E entity, S entityRenderState, float f, CallbackInfo ci) {
        var event = RenderEntityEvent.INSTANCE;
        event.setEntity$skyblock_api_client(entity);
        event.setState$skyblock_api_client(entityRenderState);
        event.post$skyblock_api_client();
        event.clear();
    }

}
