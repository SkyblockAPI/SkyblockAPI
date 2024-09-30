package tech.thatgravyboat.skyblockapi.extensions

import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

operator fun Player.get(hand: InteractionHand): ItemStack = getItemInHand(hand)
operator fun Player.get(slot: EquipmentSlot): ItemStack = getItemBySlot(slot)

