package tech.thatgravyboat.skyblockapi.mixins.debug;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentDataAttachable;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentViewable;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentViewerCategory;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ComponentViewerData;
import tech.thatgravyboat.skyblockapi.impl.debug.components.DataResultComponentData;
import tech.thatgravyboat.skyblockapi.impl.debug.components.ItemDataCategory;
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugAccessor;
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory;
import tech.thatgravyboat.skyblockapi.impl.debug.components.JsonComponentData;
import tech.thatgravyboat.skyblockapi.impl.debug.components.NbtComponentData;
import tech.thatgravyboat.skyblockapi.impl.debug.components.TextComponentData;
import tech.thatgravyboat.skyblockapi.utils.json.Json;
import tech.thatgravyboat.skyblockapi.utils.json.LenientHolderLookupAdapter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Mixin(ItemStack.class)
abstract class ItemStackComponentViewerMixin implements ComponentViewable, ItemDebugAccessor, ComponentDataAttachable {

    @Shadow
    @Final
    public static Codec<ItemStack> CODEC;

    @Override
    public @NotNull Map<@NotNull ComponentViewerCategory, @NotNull ComponentViewerData> skyblockapi$getComponents() {
        var map = new HashMap<ComponentViewerCategory, ComponentViewerData>();
        var data = CODEC.encodeStart(RegistryOps.create(NbtOps.INSTANCE, new LenientHolderLookupAdapter(Json.INSTANCE.getRegistry$skyblock_api())), (ItemStack) (Object) this);
        map.put(ItemDataCategory.INSTANCE, new DataResultComponentData(data.map(NbtComponentData::new)));

        var debugEntries = skyblockapi$getEntries();
        if (debugEntries != null) {
            for (Map.Entry<@NotNull ItemDebugCategory, @NotNull Collection<Component>> entry : debugEntries.asMap().entrySet()) {
                map.put(entry.getKey(), new TextComponentData(entry.getValue()));
            }
        }

        var componentMap = getSkyblockapi$getComponentMap();
        if (componentMap != null) {
            map.putAll(componentMap);
        }
        return map;
    }
}
