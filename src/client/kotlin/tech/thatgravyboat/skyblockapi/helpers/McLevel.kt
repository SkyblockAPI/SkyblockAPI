package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.core.BlockPos
import net.minecraft.core.RegistryAccess
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

object McLevel {

    val hasLevel: Boolean
        get() = McClient.self.level != null

    val self: Level
        get() = McClient.self.level!!

    val registry: RegistryAccess
        get() = self.registryAccess()

    operator fun get(pos: BlockPos): BlockState = self.getBlockState(pos)
}
