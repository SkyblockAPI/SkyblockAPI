package tech.thatgravyboat.skyblockapi.api.events.entity

import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.helpers.getAttachedTo
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped


internal interface ListenForNameChange {

    fun `skyblockapi$markAsNameTag`()
    fun `skyblockapi$unmarkNameTag`()
    fun `skyblockapi$isNameTag`(): Boolean

}

// TODO: actually implement the debug for this? no clue what its even supposed to do
@Module
object EntityEvents {
    val debug by debugToggle("mob_attachments", "Note: does nothing right now")
    @JvmField
    var remainingPerTick: Long = 40

    @Subscription
    context(_: TickEvent)
    fun tick() {
        remainingPerTick = 40
    }
    @Subscription(priority = Subscription.HIGHEST)
    fun onNameAttach(event: ComponentAttachEvent) {
        if (event.literalComponent.trim().startsWith("[Lv")) {
            event.cancel()
            EntityInfoLineAttachEvent(event.component, event.infoLineEntity).post(SkyBlockAPI.eventBus)
            return
        }
    }

}

open class EntityInfoLineEvent(
    val component: Component,
    val infoLineEntity: Entity,
) : CancellableSkyBlockEvent() {
    val attachedTo: Entity? get() = infoLineEntity.getAttachedTo()
    val literalComponent = component.stripped
}

class EntityInfoLineAttachEvent(
    component: Component,
    infoLineEntity: Entity,
) : EntityInfoLineEvent(component, infoLineEntity)

class NameChangedEvent(
    component: Component,
    infoLineEntity: Entity,
) : EntityInfoLineEvent(component, infoLineEntity)

class ComponentAttachEvent(
    component: Component,
    infoLineEntity: Entity,
) : EntityInfoLineEvent(component, infoLineEntity)
