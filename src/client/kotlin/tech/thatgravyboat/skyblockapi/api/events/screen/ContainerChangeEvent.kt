package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class ContainerChangeEvent(val item: ItemStack, val slot: Int, val title: Component, val inventory: List<ItemStack>) : SkyBlockEvent()
