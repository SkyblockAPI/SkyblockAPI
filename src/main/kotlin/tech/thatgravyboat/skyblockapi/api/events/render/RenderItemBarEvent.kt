package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public class RenderItemBarEvent(
    public val item: ItemStack,
    public var color: Int,
    public var percent: Float,
) : SkyBlockEvent()
