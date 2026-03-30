package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

typealias ScoreboardChangeEvent = ScoreboardUpdateEvent

/**
 * Fired when the scoreboard changes in SkyBlock.
 */
data class ScoreboardUpdateEvent(
    val old: List<String>,
    val new: List<String>,
    val oldComponents: List<Component>,
    val components: List<Component>,
) : SkyBlockEvent() {

    val added: List<String> = new - old.toSet()
    val removed: List<String> = old - new.toSet()

    val addedComponents: List<Component> = components - oldComponents.toSet()
    val removedComponents: List<Component> = oldComponents - addedComponents.toSet()
}
