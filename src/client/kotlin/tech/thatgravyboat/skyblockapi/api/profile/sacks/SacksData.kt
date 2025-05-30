package tech.thatgravyboat.skyblockapi.api.profile.sacks

import tech.thatgravyboat.skyblockapi.RemoveNextVersion

@RemoveNextVersion
data class SacksData(
    var items: MutableMap<String, Int> = mutableMapOf(),
)
