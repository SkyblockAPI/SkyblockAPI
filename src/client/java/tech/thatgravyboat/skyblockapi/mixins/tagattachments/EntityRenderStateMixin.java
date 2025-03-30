package tech.thatgravyboat.skyblockapi.mixins.tagattachments;

import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tech.thatgravyboat.skyblockapi.EntityRenderAccessor;

@Mixin(EntityRenderState.class)
public class EntityRenderStateMixin implements EntityRenderAccessor {
    @Unique
    private Entity self;

    @Override
    public void skyblockapi$setSelf(Entity entity) {
        self = entity;
    }

    @Override
    public Entity skyblockapi$getSelf() {
        return self;
    }
}
