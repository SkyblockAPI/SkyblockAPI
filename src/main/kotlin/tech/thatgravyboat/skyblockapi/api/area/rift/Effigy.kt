package tech.thatgravyboat.skyblockapi.api.area.rift

import net.minecraft.core.BlockPos

public data class Effigy(val pos: BlockPos) {

    public constructor(x: Int, y: Int, z: Int) : this(BlockPos(x, y, z))

    var enabled: Boolean = false
        internal set

}
