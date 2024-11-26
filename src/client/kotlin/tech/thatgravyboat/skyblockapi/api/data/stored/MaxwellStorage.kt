package tech.thatgravyboat.skyblockapi.api.data.stored

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.MaxwellData
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPower
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPowers
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuning
import tech.thatgravyboat.skyblockapi.utils.extentions.isSameItem
import kotlin.math.absoluteValue

private const val MAX_ACCESSORIES_PER_PAGE = 9 * 5
private const val MINIMUM_DIFFERENCE_TUNING_CHANGE = 1.5

internal object MaxwellStorage {

    private val DATA = StoredProfileData(
        ::MaxwellData,
        MaxwellData.CODEC,
        "maxwell.json"
    )

    private inline val data: MaxwellData? get() = DATA.get()

    var power: MaxwellPower
        get() = data?.power ?: MaxwellPowers.NO_POWER
        private set(value) {
            data?.power = value
        }

    var magicalPower: Int
        get() = data?.magicalPower ?: 0
        private set(value) {
            data?.magicalPower = value
        }

    val accessories: MutableList<ItemStack>
        get() = data?.accessories ?: mutableListOf()

    val unlockedPowers: MutableSet<MaxwellPower>
        get() = data?.unlockedPowers ?: mutableSetOf()

    var tunings: List<MaxwellTuning>
        get() = data?.tunings ?: emptyList()
        private set(value) {
            data?.tunings = value.toMutableList()
        }

    fun updatePower(newPower: MaxwellPower) {
        if (power == newPower) return
        power = newPower
        addUnlockedPower(newPower)
        save()
    }

    fun updateMagicalPower(newMagicalPower: Int) {
        if (magicalPower == newMagicalPower) return
        magicalPower = newMagicalPower
        save()
    }

    fun updateAccessories(page: Int, newAccessories: List<ItemStack>) {
        val firstIndex = (page - 1) * MAX_ACCESSORIES_PER_PAGE
        var shouldSave = false
        for (i in newAccessories.indices) {
            val newIndex = firstIndex + i
            if (newIndex < accessories.size) {
                if (accessories[newIndex].isSameItem((newAccessories[i]))) continue
                shouldSave = true
                accessories[newIndex] = newAccessories[i]
            } else {
                accessories.add(newAccessories[i])
                shouldSave = true
            }
        }
        if (shouldSave) save()
    }

    fun addUnlockedPower(power: MaxwellPower) {
        val shouldSave = unlockedPowers.add(power)
        if (shouldSave) save()
    }

    fun updateTunings(newTunings: List<MaxwellTuning>, exact: Boolean) {
        if (tunings == newTunings) return
        if (exact) return setNewTunings(newTunings)
        if (tunings.size != newTunings.size) return setNewTunings(newTunings)
        val oldStatMap = tunings.associateBy(MaxwellTuning::stat)
        val newStatMap = newTunings.associateBy(MaxwellTuning::stat)
        for (stat in MaxwellTuning.ALLOWED_STATS) {
            val oldValue = oldStatMap[stat]?.value
            val newValue = newStatMap[stat]?.value
            if (oldValue == newValue) continue
            if (oldValue == null || newValue == null) return setNewTunings(newTunings)
            if ((newValue - oldValue).absoluteValue > MINIMUM_DIFFERENCE_TUNING_CHANGE) {
                return setNewTunings(newTunings)
            }
        }
    }

    private fun setNewTunings(newTunings: List<MaxwellTuning>) {
        tunings = newTunings
        save()
    }

    fun reset() {
        power = MaxwellPowers.NO_POWER
        magicalPower = 0
        accessories.clear()
        unlockedPowers.clear()
        tunings = emptyList()
        save()
    }

    private fun save() = DATA.save()
}
