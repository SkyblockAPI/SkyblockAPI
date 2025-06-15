package tech.thatgravyboat.skyblockapi.mixins.events;

import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.events.render.LivingEntityRenderEvent;

@Mixin(LivingEntityRenderer.class)
public class LivingEntityRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;F)V", at = @At("TAIL"))
    public <E extends LivingEntity, S extends LivingEntityRenderState> void extractRenderState(
        E entity,
        S entityRenderState,
        float f,
        CallbackInfo ci
    ) {
        var event = LivingEntityRenderEvent.INSTANCE;
        event.setEntity(entity);
        event.setState(entityRenderState);
        event.post$sbapi();
        event.clear();
    }


}
