package tech.thatgravyboat.skyblockapi;

import net.minecraft.world.entity.Entity;

public interface EntityRenderAccessor {

    void ocean$setSelf(Entity entity);
    Entity ocean$getSelf();

}
