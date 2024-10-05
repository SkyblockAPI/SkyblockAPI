package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

class InventoryChangeEvent(val item: ItemStack, val slot: Int, val title: Component) : SkyblockEvent()
