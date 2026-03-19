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
        //~ if >= 1.21.11 'selfOrNull' -> 'self'
        get() = self != null

    //? if > 1.21.11 {
    val self: ClientLevel?
        get() = McClient.self.level
    //?} else {
    @Suppress("DEPRECATION_ERROR")
    @Deprecated(level = DeprecationLevel.WARNING, message = "Returns an unsafe value, will return a nullable ClientLevel in the next minecraft version!")
    val self: Level
        get() = level
    //? }

    val selfOrNull: ClientLevel?
        get() = McClient.self.level

    //? if < 26.1 {
    @Deprecated(level = DeprecationLevel.ERROR, message = "Returns an unsafe value, will be removed next minecraft version!")
    val level: ClientLevel
        get() = McClient.self.level!!
    //? }
  
    val registry: RegistryAccess
        get() = self?.registryAccess() ?: RegistryAccess.EMPTY

    operator fun get(pos: BlockPos): BlockState = selfOrNull?.getBlockState(pos) ?: Blocks.AIR.defaultBlockState()
    operator fun get(x: Int, y: Int, z: Int): BlockState = selfOrNull?.getBlockState(mutablePos.set(x, y, z)) ?: Blocks.AIR.defaultBlockState()


    val players: List<Player>
        get() = self?.players().orEmpty()

    fun <E : Entity> getEntities(entityTypeTest: EntityTypeTest<Entity, E>, aabb: AABB, predicate: (E) -> Boolean = { true }): List<E> {
        return self?.getEntities(entityTypeTest, aabb, predicate).orEmpty()
    }

    inline fun <reified E : Entity> getEntities(aabb: AABB, noinline predicate: (E) -> Boolean = { true }): List<E> {
        return getEntities(EntityTypeTest.forClass<Entity, E>(E::class.java), aabb, predicate)
    }
}
