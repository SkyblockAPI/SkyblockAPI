package tech.thatgravyboat.skyblockapi.api.profile.items.forge

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.stored.ForgeStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.IgnoreFiller
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.utils.extentions.currentInstant
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseDuration
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object ForgeAPI {

    private val durationRegex = RegexGroup.INVENTORY.group("forge").create("duration", "Time Remaining: (?<duration>.*)")

    @Subscription
    @IgnoreFiller
    @InventoryTitle("The Forge")
    fun onInvUpdate(event: InventoryChangeEvent) {
        if (!event.isInMainPart) return

        val index = event.slot.index - 9
        val id = event.item.getSkyBlockId() ?: return ForgeStorage.clearSlot(index)

        val found = durationRegex.anyMatch(event.item.getRawLore(), "duration") { (duration) ->
            val duration = duration.parseDuration() ?: return@anyMatch
            val expire = currentInstant() + duration
            ForgeStorage.setSlot(index, ForgeSlot(id, expire))
        }

        if (!found) {
            ForgeStorage.clearSlot(index)
            SkyBlockAPI.logger.warn("Unable to find duration for $id ")
        }
    }

    fun getForgeSlots(): Map<Int, ForgeSlot> = ForgeStorage.data.toMap()
    fun getForgeSlot(slot: Int): ForgeSlot? = ForgeStorage.data[slot]

    operator fun get(slot: Int): ForgeSlot? = getForgeSlot(slot)

}
