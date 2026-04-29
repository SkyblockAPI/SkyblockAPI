package tech.thatgravyboat.skyblockapi.api.area.farming.garden

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo
import tech.thatgravyboat.skyblockapi.utils.lazy.registryBoundLazy

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

    val apiId: String = "${name}_GARDEN_CHIP"
    val skyblockId = SkyBlockId.item(apiId)
    val itemStack: ItemStack by registryBoundLazy { SkyBlockItemsRepo.getItemStackOrDefault(apiId) }
    val displayName: Component by lazy { itemStack.hoverName }
}
