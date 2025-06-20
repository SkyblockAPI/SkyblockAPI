package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.sacks.SacksData

internal object SacksStorage {
    private val SACKS = StoredProfileData(
        1,
        ::SacksData,
        "sacks.json",
    ) { version ->
        when (version) {
            1 -> SacksData.CODEC
            else -> Codec.unit { SacksData() }
        }
    }

    val items: MutableMap<String, Int>
        get() = SACKS.get()?.items ?: mutableMapOf()

    fun updateItem(item: String, amount: Int) {
        if (items[item] == amount) return
        items[item] = amount
        SACKS.save()
    }

    fun updateItemValue(item: String, diff: Int) {
        val prevAmount = items[item] ?: 0
        val newAmount = (prevAmount + diff).coerceAtLeast(0)
        updateItem(item, newAmount)
    }

}

