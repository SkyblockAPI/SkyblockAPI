package tech.thatgravyboat.skyblockapi.api.area.farming.garden.pests

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI

enum class Vinyl {
    PRETTY_FLY,
    EARTHWORM_ENSEMBLE,
    CICADA_SYMPHONY,
    BUZZIN_BEATS,
    CRICKET_CHOIR,
    RODENT_REVOLUTION,
    DYNAMITES,
    BEETLE,
    SLOW_AND_GROOVY,
    WINGS_OF_HARMONY,
    ;

    val displayName: Component by lazy { RepoItemsAPI.getItemName(name) }
    val apiId: String = "VINYL_$name"
    val itemStack by RepoItemsAPI.getItemLazy(apiId)
}
