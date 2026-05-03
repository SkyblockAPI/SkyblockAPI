package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

typealias ScoreboardChangeEvent = ScoreboardUpdateEvent

/**
 * Fired when the scoreboard changes in SkyBlock.
 */
data class ScoreboardUpdateEvent(
    val old: List<String>,
    val new: List<String>,
    val oldComponents: List<Component>,
    val newComponents: List<Component>,
) : SkyBlockEvent() {

    @RemoveNextVersion @Deprecated("Use newComponents instead", ReplaceWith("newComponents"))
    val components: List<Component> get() = newComponents

    val added: List<String> = new - old.toSet()
    val removed: List<String> = old - new.toSet()

    val addedComponents: List<Component> = newComponents - oldComponents.toSet()
    val removedComponents: List<Component> = oldComponents - newComponents.toSet()
}
