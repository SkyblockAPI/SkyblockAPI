package tech.thatgravyboat.skyblockapi.api.area.farming.garden.pests

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo
import tech.thatgravyboat.skyblockapi.utils.lazy.registryBoundLazy

public enum class Spray {
    HONEY_JAR,
    DUNG,
    PLANT_MATTER,
    COMPOST,
    CHEESE_FUEL,
    JELLY,
    ;

    public val itemStack: ItemStack by registryBoundLazy { SkyBlockItemsRepo.getItemStackOrDefault(name) }
    public val displayName: Component by lazy { itemStack.hoverName }
}
