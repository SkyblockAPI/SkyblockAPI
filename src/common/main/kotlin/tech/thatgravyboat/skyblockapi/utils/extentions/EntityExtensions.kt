package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attributes
import tech.thatgravyboat.skyblockapi.hooks.AttributeInstanceHook
import tech.thatgravyboat.skyblockapi.hooks.DataItemHook
import tech.thatgravyboat.skyblockapi.mixins.accessors.LivingEntityAccessor
import tech.thatgravyboat.skyblockapi.mixins.accessors.SynchedEntityDataAccessor

fun LivingEntity.getHelmet() = getItemBySlot(EquipmentSlot.HEAD)
fun LivingEntity.getChestplate() = getItemBySlot(EquipmentSlot.CHEST)
fun LivingEntity.getLeggings() = getItemBySlot(EquipmentSlot.LEGS)
fun LivingEntity.getBoots() = getItemBySlot(EquipmentSlot.FEET)

fun LivingEntity.getArmor() = listOf(getHelmet(), getChestplate(), getLeggings(), getBoots())

val LivingEntity.serverMaxHealth: Float
    get() {
        val attribute = this.getAttribute(Attributes.MAX_HEALTH) ?: return this.maxHealth
        return (attribute as? AttributeInstanceHook)?.`skyblockapi$getServerValue`()?.toFloat() ?: this.maxHealth
    }
val LivingEntity.serverHealth: Float
    get() {
        val accessor = this.entityData as? SynchedEntityDataAccessor ?: return this.health
        val item = accessor.`skyblockapi$getItem`(LivingEntityAccessor.`skyblockapi$getDataHealth`())
        val hook = item as? DataItemHook<*> ?: return this.health
        return hook.`skyblockapi$getServerValue`() as? Float ?: this.health
    }
