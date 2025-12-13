package tech.thatgravyboat.skyblockapi.mixins.accessors;

import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.world.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface LivingEntityAccessor {

    @Accessor("DATA_HEALTH_ID")
    static EntityDataAccessor<Float> skyblockapi$getDataHealth() {
        throw new UnsupportedOperationException();
    }
}
