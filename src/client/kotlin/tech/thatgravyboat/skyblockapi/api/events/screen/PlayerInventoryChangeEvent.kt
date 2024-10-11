package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class PlayerInventoryChangeEvent(val slot: Int, val item: ItemStack) : SkyBlockEvent()

class PlayerHotbarChangeEvent(val slot: Int, val item: ItemStack) : SkyBlockEvent()
