package tech.thatgravyboat.skyblockapi.impl.events

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object RenderEventHandler {

    init {
        WorldRenderEvents.AFTER_ENTITIES.register { RenderWorldEvent.AfterEntities(it).post() }
        WorldRenderEvents.AFTER_TRANSLUCENT.register { RenderWorldEvent.AfterTranslucent(it).post() }
    }

}
