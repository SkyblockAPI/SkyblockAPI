package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.platform.PlayerSkin
import tech.thatgravyboat.skyblockapi.platform.skin
import java.util.UUID

public object McPlayer {

    public val self: Player? get() = Minecraft.getInstance().player

    public val position: Vec3? get() = self?.position()

    public val name: String get() = McClient.self.gameProfile.name
    public val uuid: UUID get() = McClient.self.gameProfile.id
    public val skin: PlayerSkin? get() = McClient.self.player?.skin()

    public val menu: AbstractContainerMenu? get() = self?.containerMenu

    public val health: Int get() = self?.health?.toInt() ?: 0
    public val maxHealth: Int get() = self?.maxHealth?.toInt() ?: 0

    public val air: Int get() = self?.airSupply ?: 0
    public val maxAir: Int get() = self?.maxAirSupply ?: 0

    public val xpLevel: Int get() = self?.experienceLevel ?: 1
    public val xpLevelProgress: Float get() = self?.experienceProgress ?: 0f

    public val heldItem: ItemStack get() = self?.mainHandItem ?: ItemStack.EMPTY
    public val helmet: ItemStack get() = self?.getItemBySlot(EquipmentSlot.HEAD) ?: ItemStack.EMPTY
    public val chestplate: ItemStack get() = self?.getItemBySlot(EquipmentSlot.CHEST) ?: ItemStack.EMPTY
    public val leggings: ItemStack get() = self?.getItemBySlot(EquipmentSlot.LEGS) ?: ItemStack.EMPTY
    public val boots: ItemStack get() = self?.getItemBySlot(EquipmentSlot.FEET) ?: ItemStack.EMPTY

    public val inventory: List<ItemStack> get() = self?.inventory?.nonEquipmentItems ?: emptyList()
    public val hotbar: List<ItemStack> get() = self?.inventory?.nonEquipmentItems?.subList(0, 9) ?: List(9) { ItemStack.EMPTY }

    public fun distanceSqr(pos: Vec3): Double = position?.distanceToSqr(pos) ?: 0.0
    public fun distanceSqr(pos: BlockPos): Double = distanceSqr(Vec3(pos))

    public operator fun AABB.contains(player: McPlayer): Boolean = this.contains(player.position ?: Vec3.ZERO)
}
