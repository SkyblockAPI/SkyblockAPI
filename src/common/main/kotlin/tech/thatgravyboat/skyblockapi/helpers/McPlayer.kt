package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.Minecraft
import net.minecraft.client.resources.PlayerSkin
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.*

object McPlayer {

    val self: Player? get() = Minecraft.getInstance().player

    val position: Vec3? get() = self?.position()

    val name: String get() = McClient.self.gameProfile.name
    val uuid: UUID get() = McClient.self.gameProfile.id
    val skin: PlayerSkin? get() = McClient.self.player?.skin

    val menu: AbstractContainerMenu? get() = self?.containerMenu as AbstractContainerMenu

    val health: Int get() = self?.health?.toInt() ?: 0
    val maxHealth: Int get() = self?.maxHealth?.toInt() ?: 0

    val air: Int get() = self?.airSupply ?: 0
    val maxAir: Int get() = self?.maxAirSupply ?: 0

    val xpLevel: Int get() = self?.experienceLevel ?: 1
    val xpLevelProgress: Float get() = self?.experienceProgress ?: 0f

    val heldItem: ItemStack get() = self?.mainHandItem ?: ItemStack.EMPTY
    val helmet: ItemStack get() = self?.getItemBySlot(EquipmentSlot.HEAD) ?: ItemStack.EMPTY
    val chestplate: ItemStack get() = self?.getItemBySlot(EquipmentSlot.CHEST) ?: ItemStack.EMPTY
    val leggings: ItemStack get() = self?.getItemBySlot(EquipmentSlot.LEGS) ?: ItemStack.EMPTY
    val boots: ItemStack get() = self?.getItemBySlot(EquipmentSlot.FEET) ?: ItemStack.EMPTY

    val inventory: List<ItemStack> get() = self?.inventory?.nonEquipmentItems ?: emptyList()
    val hotbar: List<ItemStack> get() = self?.inventory?.nonEquipmentItems?.subList(0, 9) ?: List(9) { ItemStack.EMPTY }

    fun distanceSqr(pos: Vec3): Double = position?.distanceToSqr(pos) ?: 0.0
    fun distanceSqr(pos: BlockPos): Double = distanceSqr(Vec3(pos))

    operator fun AABB.contains(player: McPlayer) = this.contains(player.position ?: Vec3.ZERO)
}
