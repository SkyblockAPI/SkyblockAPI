package tech.thatgravyboat.skyblockapi.api.data.stored

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.MaxwellData
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPower
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPowers

private const val MAX_ACCESSORIES_PER_PAGE = 9 * 5

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
        val index = (page - 1) * MAX_ACCESSORIES_PER_PAGE
        val shouldSave = accessories.addAll(index, newAccessories)
        if (shouldSave) save()
    }

    fun addUnlockedPower(power: MaxwellPower) {
        val shouldSave = unlockedPowers.add(power)
        if (shouldSave) save()
    }

    private fun save() = DATA.save()
}
