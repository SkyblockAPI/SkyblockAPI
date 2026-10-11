package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import tech.thatgravyboat.skyblockapi.api.profile.hotf.HotfAPI
import tech.thatgravyboat.skyblockapi.api.profile.hotm.HotmAPI

public sealed class SkillTreeType<out API : SkillTreeAPI<*, *, *>>(api: () -> API) {

    public val api: API by lazy(api)

    public object Hotm : SkillTreeType<HotmAPI>({ HotmAPI })
    public object Hotf : SkillTreeType<HotfAPI>({ HotfAPI })

    public companion object {
        public val types: List<SkillTreeType<*>> = listOf(Hotm, Hotf)
    }
}
