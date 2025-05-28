package tech.thatgravyboat.skyblockapi.api.remote.hypixel.pricing

object Pricing {
    fun getPrice(id: String?): Long {
        val product = BazaarAPI.getProduct(id)
        if (product != null) {
            return product.sellPrice.toLong()
        }
        return LowestBinAPI.getLowestPrice(id) ?: 0L
    }
}
