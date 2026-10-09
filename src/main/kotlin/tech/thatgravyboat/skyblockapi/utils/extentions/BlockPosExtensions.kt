package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

public inline fun BlockPos.forEachRelative(distance: Int, direction: Direction, forEach: (BlockPos.MutableBlockPos) -> Unit) {
    val mutable = this.mutable()
    repeat(distance) {
        mutable.move(direction)
        forEach(mutable)
    }
}

public inline fun BlockPos.forEachBelow(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.DOWN, block)
public inline fun BlockPos.forEachAbove(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.UP, block)
public inline fun BlockPos.forEachNorth(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.NORTH, block)
public inline fun BlockPos.forEachSouth(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.SOUTH, block)
public inline fun BlockPos.forEachWest(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.WEST, block)
public inline fun BlockPos.forEachEast(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.EAST, block)

public operator fun BlockPos.component1(): Int = this.x
public operator fun BlockPos.component2(): Int = this.y
public operator fun BlockPos.component3(): Int = this.z

public operator fun BlockPos.times(multiplier: Int): BlockPos =
    BlockPos(this.x * multiplier, this.y * multiplier, this.z * multiplier)

public operator fun BlockPos.div(divisor: Int): BlockPos =
    BlockPos(this.x / divisor, this.y / divisor, this.z / divisor)

public operator fun BlockPos.plus(other: BlockPos): BlockPos =
    this.offset(other.x, other.y, other.z)

public operator fun BlockPos.minus(other: BlockPos): BlockPos =
    this.offset(-other.x, -other.y, -other.z)

public fun BlockPos.toLong(): Long = BlockPos.asLong(this.x, this.y, this.z)
public fun Long.toBlockPos(): BlockPos = BlockPos(BlockPos.getX(this), BlockPos.getY(this), BlockPos.getZ(this))
