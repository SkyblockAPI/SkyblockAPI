package tech.thatgravyboat.skyblockapi.api.area.farming.garden.pests

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI

enum class Spray {
    HONEY_JAR,
    DUNG,
    PLANT_MATTER,
    COMPOST,
    CHEESE_FUEL,
    ;

    val displayName: Component by lazy { RepoItemsAPI.getItemName(name) }
    val itemStack by RepoItemsAPI.getItemLazy(name)
}
