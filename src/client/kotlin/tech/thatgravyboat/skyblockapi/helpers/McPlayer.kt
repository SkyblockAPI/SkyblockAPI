package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu

object McPlayer {

    val self: Player?
        get() = Minecraft.getInstance().player

    val menu: AbstractContainerMenu?
        get() = self?.containerMenu
}
