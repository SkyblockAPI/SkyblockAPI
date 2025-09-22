package tech.thatgravyboat.skyblockapi.api.profile.items.accessory

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.AccessoryBagStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.IgnoreFiller
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.MustBeContainer
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
object AccessoryBagAPI {
    private val group = RegexGroup.INVENTORY.group("accessory_bag")
    private val titleRegex = group.create(
        "title",
        "Accessory Bag(?: \\((?<currentPage>.*?)/(?<maxPage>.*?)\\))?",
    )

    @Subscription
    @MustBeContainer
    @IgnoreFiller
    fun onInventory(event: InventoryChangeEvent) {
        if (event.isInBottomRow) return
        titleRegex.match(event.title) { destructured ->
            val currentPage = destructured["currentPage"]?.toIntValue() ?: 1

            AccessoryBagStorage.addItem(AccessoryBagItem(event.slot.item, currentPage, event.slot.index))
        }
    }

    fun getItems(): List<AccessoryBagItem> = AccessoryBagStorage.getItems()
}
