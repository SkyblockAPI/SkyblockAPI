package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.RegistryAccess
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.entity.EntityTypeTest
import net.minecraft.world.phys.AABB

object McLevel {

    private val mutablePos = BlockPos.MutableBlockPos()

    val hasLevel: Boolean
        get() = selfOrNull != null

    @Suppress("DEPRECATION_ERROR")
    @Deprecated(level = DeprecationLevel.ERROR, message = "Returns an unsafe value, will return a nullable ClientLevel in the next minecraft version!")
    val self: Level
        get() = level

    val selfOrNull: ClientLevel?
        get() = McClient.self.level

    @Deprecated(level = DeprecationLevel.ERROR, message = "Returns an unsafe value, will be removed next minecraft version!")
    val level: ClientLevel
        get() = McClient.self.level!!

    val registry: RegistryAccess
        @Suppress("DEPRECATION_ERROR")
        get() = self.registryAccess()

    operator fun get(pos: BlockPos): BlockState = selfOrNull?.getBlockState(pos) ?: Blocks.AIR.defaultBlockState()
    operator fun get(x: Int, y: Int, z: Int): BlockState = selfOrNull?.getBlockState(mutablePos.set(x, y, z)) ?: Blocks.AIR.defaultBlockState()


    val players: List<Player>
        get() = selfOrNull?.players().orEmpty()

    fun <E : Entity> getEntities(entityTypeTest: EntityTypeTest<Entity, E>, aabb: AABB, predicate: (E) -> Boolean = { true }): List<E> {
        return selfOrNull?.getEntities(entityTypeTest, aabb, predicate).orEmpty()
    }

    inline fun <reified E : Entity> getEntities(aabb: AABB, noinline predicate: (E) -> Boolean = { true }): List<E> {
        return getEntities(EntityTypeTest.forClass<Entity, E>(E::class.java), aabb, predicate)
    }
}
