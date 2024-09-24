package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.Minecraft
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu

object McPlayer {

    val self: Player?
        get() = Minecraft.getInstance().player

    val menu: AbstractContainerMenu?
        get() = self?.containerMenu

    val health: Int
        get() = self?.health?.toInt() ?: 0

    val maxHealth: Int
        get() = self?.maxHealth?.toInt() ?: 0
}
