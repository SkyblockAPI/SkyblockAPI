package tech.thatgravyboat.skyblockapi.api.events.entity

import net.minecraft.core.Holder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.Attribute
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public class EntityRemovedEvent(val entity: Entity) : SkyBlockEvent()
public class EntityAddedEvent(val entity: Entity) : SkyBlockEvent()


public class EntityEquipmentUpdateEvent(val entity: LivingEntity) : SkyBlockEvent()

public class EntityAttributesUpdateEvent(
    public val entity: LivingEntity,
    public val changed: Map<Holder<Attribute>, ChangedAttribute>,
) : SkyBlockEvent() {
    public data class ChangedAttribute(val old: Double, val new: Double)
}
