package tech.thatgravyboat.skyblockapi.api.remote.itemdata

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.FieldName
import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktcodecs.GenerateDispatchCodec
import tech.thatgravyboat.skyblockapi.api.data.Essence
import tech.thatgravyboat.skyblockapi.api.remote.pricing.Pricing
import tech.thatgravyboat.skyblockapi.generated.DispatchHelper
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import kotlin.reflect.KClass

@GenerateDispatchCodec(Cost::class)
enum class CostTypes(override val type: KClass<out Cost>) : DispatchHelper<Cost> {
    COINS(CoinCost::class),
    ITEM(ItemCost::class),
    ESSENCE(EssenceCost::class),
    ;

    companion object {
        fun getType(id: String) = entries.first { it.id.equals(id, true) }
    }
}

@GenerateCodec
data class EssenceCost(
    @FieldName("essence_type") val essenceType: Essence,
    val amount: Int,
) : Cost(CostTypes.ESSENCE) {
    companion object {
        val CODEC = SkyblockAPICodecs.EssenceCostCodec
    }
}

@GenerateCodec
data class ItemCost(
    @FieldName("item_id") val itemId: String,
    val amount: Int,
) : Cost(CostTypes.ITEM) {
    companion object {
        val CODEC = SkyblockAPICodecs.ItemCostCodec
    }
}

@GenerateCodec
data class CoinCost(
    @FieldName("coins") val amount: Int,
) : Cost(CostTypes.COINS) {
    companion object {
        val CODEC = SkyblockAPICodecs.CoinCostCodec
    }
}

abstract class Cost(val type: CostTypes) {
    companion object {
        val CODEC: Codec<Cost> = SkyblockAPICodecs.CostCodec.codec()

        fun calculateCost(cost: Cost) = when (cost) {
            is CoinCost -> cost.amount.toLong()
            is ItemCost -> cost.amount * Pricing.getPrice(cost.itemId)
            is EssenceCost -> cost.amount * Pricing.getPrice(cost.essenceType.bazaarId)
            else -> 0L
        }
    }
}
