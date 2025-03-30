package tech.thatgravyboat.skyblockapi.api.area.slayer

import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.getStrippedAttachedLines
import tech.thatgravyboat.skyblockapi.utils.DiscoverableValue

data class SlayerInfo(val entity: Entity) {
    private fun discoverTypeIfNeeded(): SlayerMob? {
        return SLAYER_MOBS.find { mob -> entity.getStrippedAttachedLines().any { it.contains(mob.displayName) } }
    }

    private fun discoverOwner(): String? {
        return entity.getStrippedAttachedLines()
            .find { it.startsWith("spawned by: ", ignoreCase = true) }
            ?.substringAfterLast(":")?.trim()
    }

    val owner by DiscoverableValue(::discoverOwner)
    val isOwnedByPlayer: Boolean get() = owner == McPlayer.name
    val type by DiscoverableValue(::discoverTypeIfNeeded)

    override fun toString(): String {
        return "SlayerInfo(owner=$owner, isOwnedByPlayer=$isOwnedByPlayer, type=$type)"
    }
}
