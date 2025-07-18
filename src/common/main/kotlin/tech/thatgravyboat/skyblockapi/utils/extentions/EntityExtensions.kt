package tech.thatgravyboat.skyblockapi.utils.extentions

import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity

fun LivingEntity.getHelmet() = getItemBySlot(EquipmentSlot.HEAD)
fun LivingEntity.getChestplate() = getItemBySlot(EquipmentSlot.CHEST)
fun LivingEntity.getLeggings() = getItemBySlot(EquipmentSlot.LEGS)
fun LivingEntity.getBoots() = getItemBySlot(EquipmentSlot.FEET)

fun LivingEntity.getArmor() = listOf(getHelmet(), getChestplate(), getLeggings(), getBoots())
