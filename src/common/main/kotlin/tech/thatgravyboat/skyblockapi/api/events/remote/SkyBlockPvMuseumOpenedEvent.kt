package tech.thatgravyboat.skyblockapi.api.events.remote

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class MuseumEntry(
    val id: String,
    val stacks: List<Lazy<ItemStack>>,
)

@SkyBlockPvRequired
data class SkyBlockPvMuseumOpenedEvent(
    val entries: List<MuseumEntry>,
) : SkyBlockEvent()
