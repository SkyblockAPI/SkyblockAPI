package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyblockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

/**
 * Fired when the scoreboard changes in SkyBlock.
 */
data class ScoreboardUpdateEvent(
    val old: List<String>,
    val new: List<String>,
    val components: List<Component>
) : SkyblockEvent() {

    val added: List<String> = new - old.toSet()
    val removed: List<String> = old - new.toSet()

    private val addedSet: Set<String> = added.toSet()
    private val removedSet: Set<String> = removed.toSet()

    val addedComponents: List<Component> = components.filter { it.stripped in addedSet }
    val removedComponents: List<Component> = components.filter { it.stripped in removedSet }
}
