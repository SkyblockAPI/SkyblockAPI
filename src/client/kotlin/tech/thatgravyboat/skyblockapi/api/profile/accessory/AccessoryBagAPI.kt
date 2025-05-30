package tech.thatgravyboat.skyblockapi.api.profile.accessory

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.items.accessory.AccessoryBagAPI as NewAccessoryBagAPI

@RemoveNextVersion
object AccessoryBagAPI {
    fun getItems(): List<AccessoryBagItem> = NewAccessoryBagAPI.getItems().map { (item, page, slot) -> AccessoryBagItem(item, page, slot) }
}
