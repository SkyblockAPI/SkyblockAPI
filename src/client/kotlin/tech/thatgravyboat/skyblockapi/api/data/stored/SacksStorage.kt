package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.sacks.SacksData

internal object SacksStorage {
    private val SACKS = StoredProfileData(
        ::SacksData,
        SacksData.CODEC,
        "sacks_new.json",
    )

    var items: MutableMap<String, Int>
        get() = SACKS.get()?.items ?: mutableMapOf()
        private set(value) {
            SACKS.get()?.items = value
        }

    fun updateItem(item: String, amount: Int) {
        val prevAmount = items[item] ?: 0
        if (amount == prevAmount) return
        items[item] = amount
        SACKS.save()
    }

    fun updateItemValue(item: String, diff: Int) {
        val prevAmount = items[item] ?: 0
        val newAmount = (prevAmount + diff).coerceAtLeast(0)
        updateItem(item, newAmount)
    }

}

