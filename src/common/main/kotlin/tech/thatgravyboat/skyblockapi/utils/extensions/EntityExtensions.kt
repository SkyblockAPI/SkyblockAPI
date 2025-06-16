package tech.thatgravyboat.skyblockapi.utils.extensions

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.msrandom.stub.Stub

fun LivingEntity.getHelmet() = getItemBySlot(EquipmentSlot.HEAD)
fun LivingEntity.getChestplate() = getItemBySlot(EquipmentSlot.CHEST)
fun LivingEntity.getLeggings() = getItemBySlot(EquipmentSlot.LEGS)
fun LivingEntity.getBoots() = getItemBySlot(EquipmentSlot.FEET)

fun LivingEntity.getArmor() = listOf(getHelmet(), getChestplate(), getLeggings(), getBoots())

@Stub
expect fun Entity.saveWithoutId(tag: CompoundTag): CompoundTag
