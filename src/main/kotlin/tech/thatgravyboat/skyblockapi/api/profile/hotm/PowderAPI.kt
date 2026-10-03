package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.PowderStorage
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutSlot
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.StringMatch
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrencyAPI
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeType

@Suppress("unused")
@Module
object PowderAPI : SkillTreeCurrencyAPI<PowderType, PowderAPI>(
    "powder",
    PowderStorage,
    PowderType::class,
    SkillTreeType.Hotm,
    TabWidget.POWDERS
) {

    val mithril: Long get() = getCurrent(PowderType.MITHRIL)
    val gemstone: Long get() = getCurrent(PowderType.GEMSTONE)
    val glacite: Long get() = getCurrent(PowderType.GLACITE)

    val mithrilTotal: Long get() = getTotal(PowderType.MITHRIL)
    val gemstoneTotal: Long get() = getTotal(PowderType.GEMSTONE)
    val glaciteTotal: Long get() = getTotal(PowderType.GLACITE)

    override fun LoadoutSlot.getLoadoutMatch(): StringMatch? = hotm

}
