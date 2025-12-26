package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeInstance
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.hooks.AttributeInstanceHook
import tech.thatgravyboat.skyblockapi.hooks.DataItemHook
import tech.thatgravyboat.skyblockapi.mixins.accessors.LivingEntityAccessor
import tech.thatgravyboat.skyblockapi.mixins.accessors.SynchedEntityDataAccessor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

val Entity.cleanName: String get() = name.stripped

fun LivingEntity.getHelmet(): ItemStack = getItemBySlot(EquipmentSlot.HEAD)
fun LivingEntity.getChestplate(): ItemStack = getItemBySlot(EquipmentSlot.CHEST)
fun LivingEntity.getLeggings(): ItemStack = getItemBySlot(EquipmentSlot.LEGS)
fun LivingEntity.getBoots(): ItemStack = getItemBySlot(EquipmentSlot.FEET)

fun Player.isRealPlayer(): Boolean = uuid.version() == 4

fun LivingEntity.getArmor(): List<ItemStack> = listOf(getHelmet(), getChestplate(), getLeggings(), getBoots())

val LivingEntity.serverMaxHealth: Float
    get() {
        val attribute = this.getAttribute(Attributes.MAX_HEALTH) ?: return this.maxHealth
        return (attribute as? AttributeInstanceHook)?.`skyblockapi$getServerValue`()?.toFloat() ?: this.maxHealth
    }

val AttributeInstance.serverValue: Float
    get() = (this as? AttributeInstanceHook)?.`skyblockapi$getServerValue`()?.toFloat() ?: 0.0f

val LivingEntity.serverHealth: Float
    get() {
        val accessor = this.entityData as? SynchedEntityDataAccessor ?: return this.health
        val item = accessor.`skyblockapi$getItem`(LivingEntityAccessor.`skyblockapi$getDataHealth`())
        val hook = item as? DataItemHook<*> ?: return this.health
        return hook.`skyblockapi$getServerValue`() as? Float ?: this.health
    }
