package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.CrystalStatus
import tech.thatgravyboat.skyblockapi.api.data.stored.CrystalStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore

@Module
object CrystalAPI {
    val itemRegex = Regex("^Crystal Hollows Crystals$")

    /**
     * REGEX-TEST:   Jade ✖ Not Found
     * REGEX-TEST:   Amber ✖ Not Found
     * REGEX-TEST:   Ruby ✔ Found
     * REGEX-TEST:   Jade ✔ Placed
     */
    val crystalLoreRegex = Regex("^ {2}([A-Za-z]+) (✖ Not Found|✔ Found|✔ Placed)$")

    /**
     * REGEX-TEST:                                 Jade Crystal
     * REGEX-TEST:                                 Amber Crystal
     */
    val crystalFoundRegex = Regex("^ {32}([A-Za-z]+) Crystal$")

    /**
     * REGEX-TEST: ✦ You placed the Jade Crystal!
     */
    val crystalPlacedRegex = Regex("^✦ You placed the ([A-Za-z]+) Crystal!$")

    @Subscription
    @InventoryTitle("Heart of the Mountain")
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (!itemRegex.matches(event.item.cleanName)) return

        val lore = event.item.getRawLore()
        for (line in lore) {
            crystalLoreRegex.matchEntire(line)?.let { match ->
                val crystalName = match.groupValues[1]
                val status = match.groupValues[2]
                if (CrystalStorage.setCrystalStatusByName(crystalName, when (status) {
                    "✖ Not Found" -> CrystalStatus.NOT_FOUND
                    "✔ Found" -> CrystalStatus.FOUND
                    "✔ Placed" -> CrystalStatus.PLACED
                    else -> return@let
                })) {
                    Logger.info("Updated $crystalName to $status based on inventory.")
                } else {
                    Logger.info("Failed to update crystal status for $crystalName with status $status")
                }
            }
        }
    }

    @Subscription
    fun onChatReceived(event: ChatReceivedEvent.Pre) {
        val message = event.text
        when {
            crystalFoundRegex.matches(message) -> {
                val match = crystalFoundRegex.matchEntire(message) ?: return
                val crystalName = match.groupValues[1]
                CrystalStorage.setCrystalStatusByName(crystalName, CrystalStatus.FOUND)
            }
            crystalPlacedRegex.matches(message) -> {
                val match = crystalPlacedRegex.matchEntire(message) ?: return
                val crystalName = match.groupValues[1]
                CrystalStorage.setCrystalStatusByName(crystalName, CrystalStatus.PLACED)
            }
        }
    }

}
