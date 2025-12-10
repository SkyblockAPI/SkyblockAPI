package tech.thatgravyboat.skyblockapi.api.events.level

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

open class LeftClickEvent(val stack: ItemStack) : CancellableSkyBlockEvent()

class LeftClickEntityEvent(val entity: Entity, stack: ItemStack) : LeftClickEvent(stack)
class LeftClickBlockEvent(val pos: BlockPos, stack: ItemStack) : LeftClickEvent(stack)
