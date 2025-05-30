package tech.thatgravyboat.skyblockapi.api.profile.sacks

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.profile.items.sacks.SacksAPI as NewSacksAPI

@RemoveNextVersion
object SacksAPI {
    val sackItems: Map<String, Int> get() = NewSacksAPI.sackItems
}
