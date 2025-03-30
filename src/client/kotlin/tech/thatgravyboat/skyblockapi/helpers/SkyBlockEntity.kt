package tech.thatgravyboat.skyblockapi.helpers

import net.minecraft.network.chat.Component
import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.entity.NameChangedEvent
import tech.thatgravyboat.skyblockapi.helpers.EntityAttachmentAccessor.Companion.asAccessor
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Module
object SkyBlockEntity {
    @OnlyOnSkyBlock
    @Subscription
    fun event(event: NameChangedEvent) {
        event.infoLineEntity.asAccessor().`skyblockapi$attachToClosest`()
    }

    @JvmStatic
    fun getAttachedLines(entity: Entity): List<Component> = entity.getAttachedLines()
    @JvmStatic
    fun getAttachedEntities(entity: Entity): List<Entity> = entity.asAccessor().`skyblockapi$getAttachments`()
}

internal interface EntityAttachmentAccessor {

    fun `skyblockapi$attachToClosest`()
    fun `skyblockapi$getAttachments`(): List<Entity>
    fun `skyblockapi$getAttachedTo`(): Entity?

    companion object {
        fun Entity.asAccessor(): EntityAttachmentAccessor {
            return this as EntityAttachmentAccessor
        }
    }

}

fun Entity.getMobLevel(): Int? {
    return this.getAttachedLines().mapNotNull {
        it.string.replace(Regex(".*(\\[Lv\\d+]).*"), "$1").takeIf(String::isNotBlank)
    }.map { it.replace(Regex("\\D"), "") }.map { it.toInt() }.firstOrNull()
}
fun Entity.getAttachedTo(): Entity? = this.asAccessor().`skyblockapi$getAttachedTo`()
fun Entity.getStrippedAttachedLines(): List<String> = this.getAttachedLines().map { it.stripped }
fun Entity.getAttachedLines(): List<Component> = this.asAccessor().`skyblockapi$getAttachments`().mapNotNull { it.customName }
