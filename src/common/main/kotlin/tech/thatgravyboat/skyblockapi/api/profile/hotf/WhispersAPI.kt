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
    TabWidget.FOREST_WHISPERS,
    WhisperStorage,
    WhisperType::class,
    SkillTreeType.Hotf,
) {

    val forest: Long
        get() = getCurrent(WhisperType.FOREST)
    val forestTotal: Long
        get() = getCurrent(WhisperType.FOREST)

}
