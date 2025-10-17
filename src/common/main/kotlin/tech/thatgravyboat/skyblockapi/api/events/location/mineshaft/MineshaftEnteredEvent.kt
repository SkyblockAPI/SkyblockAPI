package tech.thatgravyboat.skyblockapi.api.events.location.mineshaft

import tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft.MineshaftType
import tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft.MineshaftVariant
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent

data class MineshaftEnteredEvent(val type: MineshaftType?, val variant: MineshaftVariant?) : SkyBlockEvent() {
    val isCrystal: Boolean = variant == MineshaftVariant.CRYSTAL
}
