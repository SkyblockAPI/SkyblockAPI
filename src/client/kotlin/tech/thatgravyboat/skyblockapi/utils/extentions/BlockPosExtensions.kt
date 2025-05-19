package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction

fun BlockPos.forEachRelative(distance: Int, direction: Direction, forEach: (BlockPos.MutableBlockPos) -> Unit) {
    val mutable = this.mutable()
    repeat(distance) {
        mutable.move(direction)
        forEach(mutable)
    }
}

fun BlockPos.forEachBelow(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.DOWN, block)
fun BlockPos.forEachAbove(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.UP, block)
fun BlockPos.forEachNorth(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.NORTH, block)
fun BlockPos.forEachSouth(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.SOUTH, block)
fun BlockPos.forEachWest(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.WEST, block)
fun BlockPos.forEachEast(distance: Int, block: (BlockPos.MutableBlockPos) -> Unit) = forEachRelative(distance, Direction.EAST, block)

operator fun BlockPos.component1(): Int = this.x
operator fun BlockPos.component2(): Int = this.y
operator fun BlockPos.component3(): Int = this.z

operator fun BlockPos.times(multiplier: Int): BlockPos =
    BlockPos(this.x * multiplier, this.y * multiplier, this.z * multiplier)

operator fun BlockPos.div(divisor: Int): BlockPos =
    BlockPos(this.x / divisor, this.y / divisor, this.z / divisor)

operator fun BlockPos.plus(other: BlockPos): BlockPos =
    this.offset(other.x, other.y, other.z)

operator fun BlockPos.minus(other: BlockPos): BlockPos =
    this.offset(-other.x, -other.y, -other.z)

fun BlockPos.toLong(): Long = BlockPos.asLong(this.x, this.y, this.z)
fun Long.toBlockPos(): BlockPos = BlockPos(BlockPos.getX(this), BlockPos.getY(this), BlockPos.getZ(this))
