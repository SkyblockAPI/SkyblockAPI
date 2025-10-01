package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.entity.state.PlayerRenderState

object PlayerRenderEvent : BaseRenderEntityEvent<AbstractClientPlayer, PlayerRenderState>() {
    override var state: PlayerRenderState? = null
    override var entity: AbstractClientPlayer? = null
}
