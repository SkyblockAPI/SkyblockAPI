package tech.thatgravyboat.skyblockapi.api.remote.itemdata

import com.mojang.serialization.Codec
import com.mojang.serialization.MapCodec
import com.mojang.serialization.codecs.RecordCodecBuilder
import tech.thatgravyboat.skyblockapi.api.data.Essence
import tech.thatgravyboat.skyblockapi.api.item.calculator.Pricing
import tech.thatgravyboat.skyblockapi.generated.KCodec
import tech.thatgravyboat.skyblockapi.kcodec.IncludedCodec

enum class CostTypes(val codec: MapCodec<out Cost>) {
    COINS(CoinCost.CODEC),
    ITEM(ItemCost.CODEC),
    ESSENCE(EssenceCost.CODEC),
    ;
}

data class EssenceCost(val essenceType: Essence, val amount: Int) : Cost(CostTypes.ESSENCE) {
    companion object {
        @IncludedCodec
        val CODEC: MapCodec<EssenceCost> = RecordCodecBuilder.mapCodec {
            it.group(
                KCodec.getCodec<Essence>().fieldOf("essence_type").forGetter(EssenceCost::essenceType),
                Codec.INT.fieldOf("amount").forGetter(EssenceCost::amount),
            ).apply(it, ::EssenceCost)
        }
    }
}

data class ItemCost(val itemId: String, val amount: Int) : Cost(CostTypes.ITEM) {
    companion object {
        val CODEC: MapCodec<ItemCost> = RecordCodecBuilder.mapCodec {
            it.group(
                Codec.STRING.fieldOf("item_id").forGetter(ItemCost::itemId),
                Codec.INT.fieldOf("amount").forGetter(ItemCost::amount),
            ).apply(it, ::ItemCost)
        }
    }
}

data class CoinCost(val amount: Int) : Cost(CostTypes.COINS) {
    companion object {
        val CODEC: MapCodec<CoinCost> = Codec.INT.fieldOf("coins").xmap(::CoinCost, CoinCost::amount)
    }
}

abstract class Cost(val type: CostTypes) {
    companion object {
        @IncludedCodec
        val CODEC: Codec<Cost> = Codec.STRING.dispatch({ it.type.name }, { CostTypes.valueOf(it).codec })

        fun calculateCost(cost: Cost) = when (cost) {
            is CoinCost -> cost.amount.toLong()
            is ItemCost -> cost.amount * Pricing.getPrice(cost.itemId)
            is EssenceCost -> cost.amount * Pricing.getPrice(cost.essenceType.bazaarId)
            else -> 0L
        }
    }
}
