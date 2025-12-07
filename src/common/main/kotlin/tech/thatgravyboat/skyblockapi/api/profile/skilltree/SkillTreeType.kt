package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfAPI
import tech.thatgravyboat.skyblockapi.api.profile.hotm.HotmAPI

sealed class SkillTreeType<out API : SkillTreeAPI<*, *, *>>(api: () -> API) {

    val api: API by lazy(api)

    object Hotm : SkillTreeType<HotmAPI>({ HotmAPI })
    object Hotf : SkillTreeType<HotfAPI>({ HotfAPI })

    companion object {
        val types: List<SkillTreeType<*>> = listOf(Hotm, Hotf)
    }
}
