package tech.thatgravyboat.skyblockapi

import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.render.RenderWorldEvent

object VersionedSkyblockAPI {
    @JvmStatic
    @ApiStatus.Internal
    fun init() {
        WorldRenderEvents.START.register {
            RenderWorldEvent.Start.post(SkyBlockAPI.eventBus)
        }

        WorldRenderEvents.AFTER_ENTITIES.register {
            RenderWorldEvent.AfterEntities(
                it.matrixStack() ?: return@register,
                it.consumers() ?: return@register,
                it.camera().position,
                it.camera().rotation(),
                it.tickCounter().getGameTimeDeltaPartialTick(false),
            ).post(SkyBlockAPI.eventBus)
        }

        WorldRenderEvents.AFTER_TRANSLUCENT.register {
            RenderWorldEvent.AfterTranslucent(
                it.matrixStack() ?: return@register,
                it.consumers() ?: return@register,
                it.camera().position,
                it.camera().rotation(),
                it.tickCounter().getGameTimeDeltaPartialTick(false),
            ).post(SkyBlockAPI.eventBus)
        }
    }
}
