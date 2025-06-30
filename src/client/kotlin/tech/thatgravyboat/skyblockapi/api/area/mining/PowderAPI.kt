package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.stored.PowderStorage

@RemoveNextVersion(ReplaceWith("PowderAPI", "tech.thatgravyboat.skyblockapi.api.profile.hotm.PowderAPI"))
object PowderAPI {

    val mithril: Long get() = PowderStorage.mithrilCurrent

    val gemstone: Long get() = PowderStorage.gemstoneCurrent

    val glacite: Long get() = PowderStorage.glaciteCurrent

}
