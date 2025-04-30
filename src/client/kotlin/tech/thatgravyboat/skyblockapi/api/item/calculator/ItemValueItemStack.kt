package tech.thatgravyboat.skyblockapi.api.item.calculator

import net.minecraft.world.item.ItemStack

internal interface ItemValueItemStack {

    fun `skyblockapi$getItemValueResult`(): ItemValueResult?
}

fun ItemStack.getItemValue(): ItemValueResult = (this as? ItemValueItemStack)?.`skyblockapi$getItemValueResult`() ?: ItemValueResult.EMPTY
