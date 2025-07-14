package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.entity.state.HumanoidRenderState
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.client.renderer.entity.state.PlayerRenderState
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

abstract class BaseRenderEntityEvent<E : Entity, S : EntityRenderState>() : SkyBlockEvent() {
    abstract var state: S?
        internal set
    abstract var entity: E?
        internal set

    @ApiStatus.Internal
    fun setState(state: S?) {
        this.state = state
    }

    @ApiStatus.Internal
    fun setEntity(entity: E?) {
        this.entity = entity
    }

    fun clear() {
        entity = null
        entity = null
    }
}

object RenderEntityEvent : BaseRenderEntityEvent<Entity, EntityRenderState>() {
    override var state: EntityRenderState? = null
    override var entity: Entity? = null
}

object LivingEntityRenderEvent : BaseRenderEntityEvent<LivingEntity, LivingEntityRenderState>() {
    override var state: LivingEntityRenderState? = null
    override var entity: LivingEntity? = null
}

object PlayerRenderEvent : BaseRenderEntityEvent<AbstractClientPlayer, PlayerRenderState>() {
    override var state: PlayerRenderState? = null
    override var entity: AbstractClientPlayer? = null
}

object HumanoidRenderEvent : BaseRenderEntityEvent<LivingEntity, HumanoidRenderState>() {
    override var state: HumanoidRenderState? = null
    override var entity: LivingEntity? = null
}
