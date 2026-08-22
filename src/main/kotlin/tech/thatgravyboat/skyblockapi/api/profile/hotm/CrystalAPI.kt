package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.CrystalStatus
import tech.thatgravyboat.skyblockapi.api.data.stored.CrystalStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.Logger
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.forEachMatch
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen

@Module
object CrystalAPI {

    private val inventoryGroup = RegexGroup.INVENTORY.group("crystal")
    private val chatGroup = RegexGroup.CHAT.group("crystal")
    private val widgetGroup = RegexGroup.TABLIST_WIDGET.group("crystal")

    private val itemRegex = inventoryGroup.create("hotmItem", "Crystal Hollows Crystals")

    /**
     * REGEX-TEST:   Jade ✖ Not Found
     * REGEX-TEST:   Amber ✖ Not Found
     * REGEX-TEST:   Ruby ✔ Found
     * REGEX-TEST:   Jade ✔ Placed
     */
    private val crystalLoreRegex = inventoryGroup.create("crystalLore", " {1,2}(?<name>[A-Za-z]+) (?<status>✖ Not Found|✔ Found|✔ Placed)")

    /**
     * REGEX-TEST:                                 Jade Crystal
     * REGEX-TEST:                                 Amber Crystal
     */
    private val crystalFoundRegex = chatGroup.create("crystalFound", " {32}(?<name>[A-Za-z]+) Crystal")

    /**
     * REGEX-TEST: ✦ You placed the Jade Crystal!
     */
    private val crystalPlacedRegex = chatGroup.create("crystalPlaced", "✦ You placed the (?<name>[A-Za-z]+) Crystal!")

    @Subscription
    @InventoryTitle("Heart of the Mountain")
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (!itemRegex.matches(event.item.cleanName)) return

        val lore = event.item.getRawLore()
        crystalLoreRegex.forEachMatch(lore, "name", "status") { (name, status) ->
            if (CrystalStorage.setCrystalStatusByName(name, CrystalStatus.fromString(status) ?: return@forEachMatch)) {
                Logger.info("Updated $name to $status based on inventory.")
            } else {
                Logger.info("Failed to update crystal status for $name with status $status")
            }
        }
    }

    @Subscription
    fun onChatReceived(event: ChatReceivedEvent.Pre) {
        matchWhen(event.text) {
            case(crystalFoundRegex, "name") { (name) ->
                CrystalStorage.setCrystalStatusByName(name, CrystalStatus.FOUND)
            }
            case(crystalPlacedRegex, "name") { (name) ->
                CrystalStorage.setCrystalStatusByName(name, CrystalStatus.PLACED)
            }
        }
    }

    @Subscription
    @OnlyWidget(TabWidget.CRYSTALS)
    fun onTabWidget(event: TabWidgetChangeEvent) {
        crystalLoreRegex.forEachMatch(event.new, "name", "status") { (name, status) ->
            if (CrystalStorage.setCrystalStatusByName(name, CrystalStatus.fromString(status) ?: return@forEachMatch)) {
                Logger.info("Updated $name to $status based on inventory.")
            } else {
                Logger.info("Failed to update crystal status for $name with status $status")
            }
        }
    }

}
