package tech.thatgravyboat.skyblockapi.api.area.farming.garden.pests

import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo

enum class Spray {
    HONEY_JAR,
    DUNG,
    PLANT_MATTER,
    COMPOST,
    CHEESE_FUEL,
    JELLY,
    ;

    val itemStack by lazy { SkyBlockItemsRepo.getItemStackOrDefault(name) }
    val displayName: Component by lazy { itemStack.hoverName }
}
