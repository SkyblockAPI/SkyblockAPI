package tech.thatgravyboat.skyblockapi.api.events.screen

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent

class ContainerInitializedEvent(val itemStacks: List<ItemStack>, val title: Component) : SkyblockEvent()
