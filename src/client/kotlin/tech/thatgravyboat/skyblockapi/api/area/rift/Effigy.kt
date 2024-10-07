package tech.thatgravyboat.skyblockapi.api.area.rift

import net.minecraft.core.BlockPos

class Effigy(val pos: BlockPos) {

    constructor(x: Int, y: Int, z: Int) : this(BlockPos(x, y, z))

    var enabled: Boolean = false
        internal set

}
