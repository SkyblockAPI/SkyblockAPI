package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfAPI
import tech.thatgravyboat.skyblockapi.api.profile.hotm.HotmAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.getSealedObjects

sealed class SkillTreeType<out API : SkillTreeAPI<*, *, *>>(val api: API) {
    object Hotm : SkillTreeType<HotmAPI>(HotmAPI)
    object Hotf : SkillTreeType<HotfAPI>(HotfAPI)

    companion object {
        val types: List<SkillTreeType<*>> = getSealedObjects()
    }
}
