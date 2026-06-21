package tech.thatgravyboat.skyblockapi.mixins.debug;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentDataAttachable;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentViewerCategory;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentViewerData;

import java.util.HashMap;
import java.util.Map;

@Mixin({Entity.class, ItemStack.class})
public class ComponentViewerMixin implements ComponentDataAttachable {

    @Unique
    private Map<ComponentViewerCategory, ComponentViewerData> skyblockapi$componentMap;

    @Override
    public void skyblockapi$addComponent(@NotNull ComponentViewerCategory category, @NotNull ComponentViewerData entry) {
        if (skyblockapi$componentMap == null) {
            this.skyblockapi$componentMap = new HashMap<>();
        }
        skyblockapi$componentMap.put(category, entry);
    }

    @Override
    public @Nullable Map<@NotNull ComponentViewerCategory, @NotNull ComponentViewerData> getSkyblockapi$getComponentMap() {
        return this.skyblockapi$componentMap;
    }
}
