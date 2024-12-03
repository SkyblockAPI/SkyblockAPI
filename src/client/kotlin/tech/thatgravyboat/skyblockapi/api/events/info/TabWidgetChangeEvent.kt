package tech.thatgravyboat.skyblockapi.api.events.info

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.events.base.EventBus
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedName

data class TabWidgetChangeEvent(
    val widget: TabWidget,
    val old: List<String>,
    val new: List<String>,
    val newComponents: List<Component>,
) : SkyBlockEvent() {

    override fun post(bus: EventBus): Boolean =
        bus.post(this, this.widget)
}

enum class TabWidget {
    AREA,
    PROFILE,
    PET,
    DAILY_QUESTS,
    FORGES,
    COMMISSIONS,
    SKILLS,
    POWDERS,
    ELECTION,
    CRYSTALS,
    BESTIARY,
    COLLECTION,
    STATS,
    EVENT,
    PARTY,
    MINIONS,
    DUNGEONS,
    ESSENCE,
    GOOD_TO_KNOW,
    SHEN,
    ADVERTISEMENT,
    TRAPPER,
    EVENT_TRACKERS,
    FROZEN_CORPSES,
    ACTIVE_EFFECTS,
    ;

    private val string = toFormattedName()

    override fun toString() = string
}
