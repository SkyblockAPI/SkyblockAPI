package tech.thatgravyboat.skyblockapi.api.events.entity

import net.minecraft.core.Holder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class EntityRemovedEvent(val entity: Entity) : SkyBlockEvent()
class EntityAddedEvent(val entity: Entity) : SkyBlockEvent()


class EntityEquipmentUpdateEvent(val entity: LivingEntity) : SkyBlockEvent()

class EntityAttributesUpdateEvent(
    val entity: LivingEntity,
    val changed: Map<Holder<Attribute>, ChangedAttribute>,
) : SkyBlockEvent() {
    data class ChangedAttribute(val old: Double, val new: Double)
}
