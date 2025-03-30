package tech.thatgravyboat.skyblockapi.api.area.slayer

import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.getStrippedAttachedLines
import tech.thatgravyboat.skyblockapi.utils.DiscoverableVariable

class SlayerInfo(val entity: Entity) {
    private fun discoverTypeIfNeeded(): SlayerMob? {
        entity.getStrippedAttachedLines().forEach { line ->
            SLAYER_MOBS.find { line.contains(it.displayName) }?.let {
                return it
            }
        }
        return null
    }

    private fun discoverOwner(): String? {
        return entity.getStrippedAttachedLines()
            .find { it.startsWith("spawned by: ", ignoreCase = true) }
            ?.substringAfterLast(":")?.trim()
    }

    val owner by DiscoverableVariable(::discoverOwner)
    val isOwnedByPlayer: Boolean get() = owner == McPlayer.name
    val type by DiscoverableVariable(::discoverTypeIfNeeded)

    override fun toString(): String {
        return "SlayerInfo(owner=$owner, isOwnedByPlayer=$isOwnedByPlayer, type=$type)"
    }
}
