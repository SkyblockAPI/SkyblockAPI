package tech.thatgravyboat.skyblockapi.api.events.level

import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent

public open class LeftClickEvent(public val stack: ItemStack) : CancellableSkyBlockEvent()

public class LeftClickEntityEvent(public val entity: Entity, stack: ItemStack) : LeftClickEvent(stack)
public class LeftClickBlockEvent(public val pos: BlockPos, stack: ItemStack) : LeftClickEvent(stack)
