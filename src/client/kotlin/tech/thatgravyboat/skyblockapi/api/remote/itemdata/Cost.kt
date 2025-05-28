package tech.thatgravyboat.skyblockapi.api.remote.itemdata

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.NamedCodec
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.data.Essence
import tech.thatgravyboat.skyblockapi.api.remote.pricing.Pricing
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.CoinCost as NewCoinCost
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.Cost as NewCost
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.EssenceCost as NewEssenceCost
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemCost as NewItemCost

@RemoveNextVersion(
    ReplaceWith(
        "CostTypes",
        "tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.CostTypes",
    ),
)
enum class CostTypes(val codec: MapCodec<out Cost>) {
    COINS(SkyblockAPICodecs.OldCoinCostCodec),
    ITEM(SkyblockAPICodecs.OldItemCostCodec),
    ESSENCE(SkyblockAPICodecs.OldEssenceCostCodec),
    ;
}

@RemoveNextVersion(
    ReplaceWith(
        "EssenceCost",
        "tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.EssenceCost",
    ),
)
@GenerateCodec
@NamedCodec("OldEssenceCost")
data class EssenceCost(
    @FieldName("essence_type") val essenceType: Essence,
    val amount: Int,
) : Cost(CostTypes.ESSENCE) {
    companion object {
        val CODEC = SkyblockAPICodecs.OldEssenceCostCodec
    }
}

@RemoveNextVersion(
    ReplaceWith(
        "ItemCost",
        "tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.ItemCost",
    ),
)
@GenerateCodec
@NamedCodec("OldItemCost")
data class ItemCost(
    @FieldName("item_id") val itemId: String,
    val amount: Int,
) : Cost(CostTypes.ITEM) {
    companion object {
        val CODEC = SkyblockAPICodecs.OldItemCostCodec
    }
}

@RemoveNextVersion(
    ReplaceWith(
        "CoinCost",
        "tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.CoinCost",
    ),
)
@GenerateCodec
@NamedCodec("OldCoinCost")
data class CoinCost(
    @FieldName("coins") val amount: Int,
) : Cost(CostTypes.COINS) {
    companion object {
        val CODEC = SkyblockAPICodecs.OldCoinCostCodec
    }
}

@RemoveNextVersion(
    ReplaceWith(
        "Cost",
        "tech.thatgravyboat.skyblockapi.api.remote.hypixel.itemdata.Cost",
    ),
)
abstract class Cost(val type: CostTypes) {
    companion object {
        fun calculateCost(cost: Cost) = when (cost) {
            is CoinCost -> cost.amount.toLong()
            is ItemCost -> cost.amount * Pricing.getPrice(cost.itemId)
            is EssenceCost -> cost.amount * Pricing.getPrice(cost.essenceType.bazaarId)
            else -> 0L
        }

        val CODEC: Codec<Cost> = Codec.STRING.dispatch({ it.type.name }, { CostTypes.valueOf(it).codec })

        internal fun fromNew(cost: NewCost?): Cost? {
            return when (cost) {
                is NewCoinCost -> CoinCost(cost.amount)
                is NewItemCost -> ItemCost(cost.itemId, cost.amount)
                is NewEssenceCost -> EssenceCost(cost.essenceType, cost.amount)
                else -> null
            }
        }
    }
}
