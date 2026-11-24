package tech.thatgravyboat.skyblockapi.api.events.entity

import net.minecraft.core.Holder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public data class EntityRemovedEvent(val entity: Entity) : SkyBlockEvent()
public data class EntityAddedEvent(val entity: Entity) : SkyBlockEvent()


public data class EntityEquipmentUpdateEvent(val entity: LivingEntity) : SkyBlockEvent()

public data class EntityAttributesUpdateEvent(
    val entity: LivingEntity,
    val changed: Map<Holder<Attribute>, ChangedAttribute>,
) : SkyBlockEvent() {
    public data class ChangedAttribute(val old: Double, val new: Double)
}
