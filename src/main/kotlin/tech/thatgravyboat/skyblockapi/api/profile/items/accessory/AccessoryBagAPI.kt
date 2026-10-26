package tech.thatgravyboat.skyblockapi.api.profile.items.accessory

import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellAPI

@Deprecated("Replaced with maxwell api", level = DeprecationLevel.HIDDEN)
public object AccessoryBagAPI {
    @Suppress("DEPRECATION")
    public fun getItems(): List<AccessoryBagItem> = MaxwellAPI.accessories.map { (item, page, slot) -> AccessoryBagItem(item, page, slot) }
}
