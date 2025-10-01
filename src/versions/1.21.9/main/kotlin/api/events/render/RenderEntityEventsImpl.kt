package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.renderer.entity.state.AvatarRenderState
import net.minecraft.world.entity.Avatar

object AvatarRenderEvent : BaseRenderEntityEvent<Avatar, AvatarRenderState>() {
    override var state: AvatarRenderState? = null
    override var entity: Avatar? = null
}
