package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

class PlayerInventoryChangeEvent(val slot: Int, val item: ItemStack) : SkyblockEvent()

class PlayerHotbarChangeEvent(val slot: Int, val item: ItemStack) : SkyblockEvent()
