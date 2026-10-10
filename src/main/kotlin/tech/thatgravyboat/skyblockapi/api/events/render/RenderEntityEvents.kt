package tech.thatgravyboat.skyblockapi.api.events.render

import net.minecraft.client.renderer.entity.state.AvatarRenderState
import net.minecraft.client.renderer.entity.state.EntityRenderState
import net.minecraft.client.renderer.entity.state.HumanoidRenderState
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState
import net.minecraft.world.entity.Avatar
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

public abstract class BaseRenderEntityEvent<E : Entity, S : EntityRenderState> : SkyBlockEvent() {
    public abstract var state: S?
        internal set
    public abstract var entity: E?
        internal set

    @ApiStatus.Internal
    public fun setState(state: S?) {
        this.state = state
    }

    @ApiStatus.Internal
    public fun setEntity(entity: E?) {
        this.entity = entity
    }

    public fun clear() {
        entity = null
        entity = null
    }
}

public object RenderEntityEvent : BaseRenderEntityEvent<Entity, EntityRenderState>() {
    override var state: EntityRenderState? = null
    override var entity: Entity? = null
}

public object LivingEntityRenderEvent : BaseRenderEntityEvent<LivingEntity, LivingEntityRenderState>() {
    override var state: LivingEntityRenderState? = null
    override var entity: LivingEntity? = null
}

public object HumanoidRenderEvent : BaseRenderEntityEvent<LivingEntity, HumanoidRenderState>() {
    override var state: HumanoidRenderState? = null
    override var entity: LivingEntity? = null
}

public object AvatarRenderEvent : BaseRenderEntityEvent<Avatar, AvatarRenderState>() {
    override var state: AvatarRenderState? = null
    override var entity: Avatar? = null
}
