package tech.thatgravyboat.skyblockapi.api.remote.itemdata

import com.mojang.serialization.MapCodec
import org.jetbrains.annotations.ApiStatus
import tech.thatgravyboat.skyblockapi.api.data.Essence
import tech.thatgravyboat.skyblockapi.api.remote.pricing.Pricing

@Deprecated("Moved to remote.hypixel.itemdata")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
enum class CostTypes(val codec: MapCodec<out tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.Cost>) {
    COINS(tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.CoinCost.CODEC),
    ITEM(tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemCost.CODEC),
    ESSENCE(tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.EssenceCost.CODEC),
    ;
}

@Deprecated("Moved to remote.hypixel.itemdata")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
data class EssenceCost(val essenceType: Essence, val amount: Int) : Cost(CostTypes.ESSENCE)

@Deprecated("Moved to remote.hypixel.itemdata")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
data class ItemCost(val itemId: String, val amount: Int) : Cost(CostTypes.ITEM)

@Deprecated("Moved to remote.hypixel.itemdata")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
data class CoinCost(val amount: Int) : Cost(CostTypes.COINS)

@Deprecated("Moved to remote.hypixel.itemdata")
@ApiStatus.ScheduledForRemoval(inVersion = "1.21.6 or 1.22")
abstract class Cost(val type: CostTypes) {
    companion object {
        fun calculateCost(cost: Cost) = when (cost) {
            is CoinCost -> cost.amount.toLong()
            is ItemCost -> cost.amount * Pricing.getPrice(cost.itemId)
            is EssenceCost -> cost.amount * Pricing.getPrice(cost.essenceType.bazaarId)
            else -> 0L
        }
    }
}
