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
public object EntityEvents {
    public val debug: Boolean by debugToggle("mob_attachments", "Note: does nothing right now")
    @JvmField
    public var remainingPerTick: Long = 40

    @Subscription
    context(_: TickEvent)
    internal fun tick() {
        remainingPerTick = 40
    }
    @Subscription(priority = Subscription.HIGHEST)
    internal fun onNameAttach(event: ComponentAttachEvent) {
        if (event.literalComponent.trim().startsWith("[Lv")) {
            event.cancel()
            EntityInfoLineAttachEvent(event.component, event.infoLineEntity).post(SkyBlockAPI.eventBus)
            return
        }
    }

}

public open class EntityInfoLineEvent(
    public val component: Component,
    public val infoLineEntity: Entity,
) : CancellableSkyBlockEvent() {
    public val attachedTo: Entity? get() = infoLineEntity.getAttachedTo()
    public val literalComponent: String = component.stripped
}

public class EntityInfoLineAttachEvent(
    component: Component,
    infoLineEntity: Entity,
) : EntityInfoLineEvent(component, infoLineEntity)

public class NameChangedEvent(
    component: Component,
    infoLineEntity: Entity,
) : EntityInfoLineEvent(component, infoLineEntity)

public class ComponentAttachEvent(
    component: Component,
    infoLineEntity: Entity,
) : EntityInfoLineEvent(component, infoLineEntity)
