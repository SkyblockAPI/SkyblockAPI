package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.RegistryAccess
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

object McLevel {

    private val mutablePos = BlockPos.MutableBlockPos()

    val hasLevel: Boolean
        get() = McClient.self.level != null

    val self: Level
        get() = level

    val level: ClientLevel
        get() = McClient.self.level!!

    val registry: RegistryAccess
        get() = self.registryAccess()

    operator fun get(pos: BlockPos): BlockState = self.getBlockState(pos)
    operator fun get(x: Int, y: Int, z: Int): BlockState = self.getBlockState(mutablePos.set(x, y, z))
}
