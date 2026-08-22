package tech.thatgravyboat.skyblockapi.mixins.debug;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentDataAttachable;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentViewable;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentViewerCategory;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentViewerData;
import tech.thatgravyboat.skyblockapi.impl.debug.components.EntityDataCategory;
import tech.thatgravyboat.skyblockapi.impl.debug.components.NbtComponentData;
import tech.thatgravyboat.skyblockapi.platform.EntityPlatformKt;

import java.util.HashMap;
import java.util.Map;

@Mixin(Entity.class)
public abstract class EntityComponentViewerMixin implements ComponentViewable, ComponentDataAttachable {
    @Override
    public @NotNull Map<@NotNull ComponentViewerCategory, @NotNull ComponentViewerData> skyblockapi$getComponents() {
        var map = new HashMap<ComponentViewerCategory, ComponentViewerData>();
        var data = EntityPlatformKt.save((Entity) (Object) this);
        map.put(EntityDataCategory.INSTANCE, new NbtComponentData(data));

        var componentMap = getSkyblockapi$getComponentMap();
        if (componentMap != null) {
            map.putAll(componentMap);
        }
        return map;
    }
}
