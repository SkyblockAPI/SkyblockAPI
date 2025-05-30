package tech.thatgravyboat.skyblockapi.api.profile.mining.forge

import kotlinx.datetime.Clock
import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.ForgeStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.getSkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.parseDuration
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object ForgeAPI {

    private val durationRegex = RegexGroup.INVENTORY.group("forge").create("duration", "Time Remaining: (?<duration>.*)")

    @Subscription
    @InventoryTitle("The Forge")
    fun onInvUpdate(event: InventoryChangeEvent) {
        if (!event.isInMainPart) return
        if (event.isSkyBlockFiller) return

        val index = event.slot.index - 9
        val id = event.item.getSkyBlockId() ?: return

        durationRegex.anyMatch(event.item.getRawLore(), "duration") { (duration) ->
            val duration = duration.parseDuration() ?: return@anyMatch
            val expire = Clock.System.now() + duration
            ForgeStorage.setSlot(index, ForgeSlot(id, expire))
        }
    }

    fun getForgeSlots() = ForgeStorage.data.entries.map { (key, value) -> value to key }

}
