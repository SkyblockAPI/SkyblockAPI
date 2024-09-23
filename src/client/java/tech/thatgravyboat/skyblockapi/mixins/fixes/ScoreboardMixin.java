package tech.thatgravyboat.skyblockapi.mixins.fixes;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.world.scores.Scoreboard;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(Scoreboard.class)
public class ScoreboardMixin {

    @WrapOperation(
        method = "addPlayerTeam",
        at = @At(value = "INVOKE", target = "Lorg/slf4j/Logger;warn(Ljava/lang/String;Ljava/lang/Object;)V")
    )
    public void addPlayerTeam(Logger instance, String s, Object o, Operation<Void> original) {
        // Do nothing
    }
}
