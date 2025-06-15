package tech.thatgravyboat.skyblockapi.api.remote.pricing

import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing as NewPricing

@RemoveNextVersion(
    replaceWith = ReplaceWith(
        "Pricing",
        "tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing.Pricing",
    ),
)
object Pricing {
    fun getPrice(id: String?) = NewPricing.getPrice(id)
}
