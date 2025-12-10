package tech.thatgravyboat.skyblockapi.api.events.entity

import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

class EntityRemovedEvent(val entity: Entity) : SkyBlockEvent()
class EntityAddedEvent(val entity: Entity) : SkyBlockEvent()
