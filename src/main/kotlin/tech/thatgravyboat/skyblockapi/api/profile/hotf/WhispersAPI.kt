package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.WhisperStorage
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutSlot
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.StringMatch
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrencyAPI
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeType

@Suppress("unused")
@Module
public data object WhispersAPI : SkillTreeCurrencyAPI<WhisperType, WhispersAPI>(
    "whispers",
    WhisperStorage,
    WhisperType::class,
    SkillTreeType.Hotf,
    TabWidget.FOREST_WHISPERS, TabWidget.DESERT_WHISPERS
) {

    public val forest: Long
        get() = getCurrent(WhisperType.FOREST)
    public val forestTotal: Long
        get() = getTotal(WhisperType.FOREST)

    public val desert: Long
        get() = getCurrent(WhisperType.DESERT)
    public val desertTotal: Long
        get() = getTotal(WhisperType.DESERT)

    override fun LoadoutSlot.getLoadoutMatch(): StringMatch? = hotm

}
