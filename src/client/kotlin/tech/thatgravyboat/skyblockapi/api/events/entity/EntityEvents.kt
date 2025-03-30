package tech.thatgravyboat.skyblockapi.api.events.entity

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.CancellableSkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.helpers.getAttachedTo
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped


internal interface ListenForNameChange {

    fun `skyblockapi$markAsNameTag`()
    fun `skyblockapi$unmarkNameTag`()
    fun `skyblockapi$isNameTag`(): Boolean

}

@Module
object EntityEvents {
    var debug: Boolean = false

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("mob_attachments") {
                callback {
                    debug = !debug
                }
            }
        }
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
    val literalComponent by lazy { component.stripped }
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
