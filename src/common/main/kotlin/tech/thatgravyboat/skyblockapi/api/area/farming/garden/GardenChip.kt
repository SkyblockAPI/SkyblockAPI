package tech.thatgravyboat.skyblockapi.api.area.farming.garden

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import kotlin.getValue

enum class GardenChip {
    CROPSHOT,
    EVERGREEN,
    HYPERCHARGE,
    MECHAMIND,
    OVERDRIVE,
    QUICKDRAW,
    RAREFINDER,
    SOWLEDGE,
    SYNTHESIS,
    VERMIN_VAPORIZER,
    ;

    val displayName: Component by lazy { RepoItemsAPI.getItemName(name) }
    val apiId: String = "${name}_GARDEN_CHIP"
    val skyblockId = SkyBlockId.item(apiId)
    val itemStack by RepoItemsAPI.getItemLazy(apiId)
}
