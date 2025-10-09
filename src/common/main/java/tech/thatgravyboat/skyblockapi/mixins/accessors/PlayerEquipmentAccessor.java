package tech.thatgravyboat.skyblockapi.mixins.accessors;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerEquipment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(PlayerEquipment.class)
public interface PlayerEquipmentAccessor {

    @Accessor("player")
    Player player();

}
