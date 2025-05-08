package tech.thatgravyboat.skyblockapi.api.area.hub

import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.remote.pricing.BazaarAPI
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
@Deprecated("Moved to remote.pricing.BazaarAPI")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
object BazaarAPI {

    val products get() = BazaarAPI.products
    fun getProduct(id: String?) = BazaarAPI.getProduct(id)
}
