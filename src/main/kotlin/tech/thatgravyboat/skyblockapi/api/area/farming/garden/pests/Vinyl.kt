package tech.thatgravyboat.skyblockapi.api.area.farming.garden.pests

import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo
import tech.thatgravyboat.skyblockapi.utils.lazy.registryBoundLazy

public enum class Vinyl {
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
    PRAY_FOR_ME,
    IMAGINE_DRAGONFLIES,
    FIREFLY
    ;

    public val apiId: String = "VINYL_$name"
    public val skyblockId: SkyBlockId = SkyBlockId.item(apiId)
    public val itemStack: ItemStack by registryBoundLazy { SkyBlockItemsRepo.getItemStackOrDefault(apiId) }
    public val displayName: Component by lazy { itemStack.hoverName }
}
