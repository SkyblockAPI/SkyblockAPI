package tech.thatgravyboat.skyblockapi.api.profile.hotf

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.WhisperStorage
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrencyAPI
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeType

@Suppress("unused")
@Module
object WhispersAPI : SkillTreeCurrencyAPI<WhisperType, WhispersAPI>(
    "whispers",
    listOf(TabWidget.FOREST_WHISPERS, TabWidget.DESERT_WHISPERS),
    WhisperStorage,
    WhisperType::class,
    SkillTreeType.Hotf,
) {

    val forest: Long
        get() = getCurrent(WhisperType.FOREST)
    val forestTotal: Long
        get() = getTotal(WhisperType.FOREST)

    val desert: Long
        get() = getCurrent(WhisperType.DESERT)
    val desertTotal: Long
        get() = getTotal(WhisperType.DESERT)

}
