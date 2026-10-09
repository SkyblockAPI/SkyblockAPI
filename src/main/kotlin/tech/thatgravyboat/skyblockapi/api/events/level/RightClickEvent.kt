package tech.thatgravyboat.skyblockapi.api.events.level

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

public open class RightClickEvent(val stack: ItemStack) : CancellableSkyBlockEvent()

public class RightClickItemEvent(stack: ItemStack) : RightClickEvent(stack)
public class RightClickBlockEvent(val pos: BlockPos, stack: ItemStack) : RightClickEvent(stack)
public class RightClickEntityEvent(val entity: Entity, stack: ItemStack) : RightClickEvent(stack)
