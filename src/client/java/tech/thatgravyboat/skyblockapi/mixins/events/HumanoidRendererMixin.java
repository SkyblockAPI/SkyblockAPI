package tech.thatgravyboat.skyblockapi.mixins.events;

import net.minecraft.client.renderer.entity.HumanoidMobRenderer;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import tech.thatgravyboat.skyblockapi.api.events.render.HumanoidRenderEvent;

@Mixin(HumanoidMobRenderer.class)
public class HumanoidRendererMixin {

    @Inject(method = "extractRenderState(Lnet/minecraft/world/entity/Mob;Lnet/minecraft/client/renderer/entity/state/HumanoidRenderState;F)V", at = @At("TAIL"))
    public <E extends Mob, S extends HumanoidRenderState> void extractRenderState(
        E entity,
        S entityRenderState,
        float f,
        CallbackInfo ci
    ) {
        var event = HumanoidRenderEvent.INSTANCE;
        event.setEntity$skyblock_api_client(entity);
        event.setState$skyblock_api_client(entityRenderState);
        event.post$skyblock_api_client();
        event.clear();
    }


}
