package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.PowderStorage
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrencyAPI
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeType

@Suppress("unused")
@Module
public object PowderAPI : SkillTreeCurrencyAPI<PowderType, PowderAPI>(
    "powder",
    listOf(TabWidget.POWDERS),
    PowderStorage,
    PowderType::class,
    SkillTreeType.Hotm,
) {

    public val mithril: Long get() = getCurrent(PowderType.MITHRIL)
    public val gemstone: Long get() = getCurrent(PowderType.GEMSTONE)
    public val glacite: Long get() = getCurrent(PowderType.GLACITE)

    public val mithrilTotal: Long get() = getTotal(PowderType.MITHRIL)
    public val gemstoneTotal: Long get() = getTotal(PowderType.GEMSTONE)
    public val glaciteTotal: Long get() = getTotal(PowderType.GLACITE)

}
