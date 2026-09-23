package tech.thatgravyboat.skyblockapi.api.data.stored

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.MaxwellData
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPower
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellPowers
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuning
import tech.thatgravyboat.skyblockapi.api.profile.maxwell.MaxwellTuningTemplate
import tech.thatgravyboat.skyblockapi.utils.extentions.isSameItem
import tech.thatgravyboat.skyblockapi.utils.extentions.replaceWith

private const val MAX_ACCESSORIES_PER_PAGE = 9 * 5

internal object MaxwellStorage {

    private val DATA = StoredProfileData<MaxwellData>("maxwell.json")

    private inline val data: MaxwellData? get() = DATA.get()

    var power: MaxwellPower
        get() = data?.power ?: MaxwellPowers.NO_POWER
        private set(value) {
            data?.power = value
        }

    var accessoryPower: Int
        get() = data?.accessoryPower ?: 0
        private set(value) {
            data?.accessoryPower = value
        }

    val accessories: MutableList<ItemStack>
        get() = data?.accessories ?: mutableListOf()

    val unlockedPowers: MutableSet<MaxwellPower>
        get() = data?.unlockedPowers ?: mutableSetOf()

    val tunings: MutableSet<MaxwellTuning>
        get() = data?.tunings ?: mutableSetOf()

    val tuningTemplates: MutableList<MaxwellTuningTemplate>
        get() = data?.tuningTemplates ?: mutableListOf()

    fun updatePower(newPower: MaxwellPower) {
        if (power == newPower) return
        power = newPower
        addUnlockedPower(newPower)
        save()
    }

    fun updateAccessoryPower(newAccessoryPower: Int) {
        if (accessoryPower == newAccessoryPower) return
        accessoryPower = newAccessoryPower
        save()
    }

    fun updateAccessory(page: Int, index: Int, accessory: ItemStack) {
        val firstIndex = (page - 1) * MAX_ACCESSORIES_PER_PAGE
        val newIndex = firstIndex + index
        if (newIndex < accessories.lastIndex) {
            while (accessories.lastIndex < newIndex) {
                accessories.add(ItemStack.EMPTY)
            }
        }
        if (accessories.getOrNull(newIndex)?.isSameItem(accessory) == true) return
        accessories[newIndex] = accessory
        save()
    }

    /** Accessories in accessory bag get all the empty items between them removed on server change */
    fun fixEmptyAccessories() {
        val removedAny = accessories.removeAll { it.isEmpty }
        if (removedAny) save()
    }

    fun addUnlockedPower(power: MaxwellPower) {
        val shouldSave = unlockedPowers.add(power)
        if (shouldSave) save()
    }

    fun removeTuning(stat: SkyBlockStat) {
        val removed = this.tunings.removeIf { it.stat == stat }
        if (removed) save()
    }

    fun setTuning(tuning: MaxwellTuning) {
        if (tuning in tunings) return
        tunings.removeIf { it.stat == tuning.stat }
        tunings.add(tuning)
        save()
    }

    fun updateTunings(newTunings: Collection<MaxwellTuning>) {
        if (tunings == newTunings) return
        this.tunings.replaceWith(newTunings)
        save()
    }

    fun updateTuningTemplateSlot(index: Int, tunings: Set<MaxwellTuning>) {
        val current = tuningTemplates.find { it.index == index }
        if (current != null) {
            if (!current.locked && current.tunings == tunings) return
            current.locked = false
            current.tunings = tunings
            save()
            return
        }
        tuningTemplates.add(MaxwellTuningTemplate(index, locked = false, tunings))
    }

    fun lockTuningTemplateSlot(index: Int) {
        val current = tuningTemplates.find { it.index == index }
        if (current != null) {
            if (current.locked && current.tunings.isEmpty()) return
            current.locked = true
            current.tunings = emptySet()
            save()
            return
        }
        tuningTemplates.add(MaxwellTuningTemplate(index))
        save()
    }

    fun reset() {
        power = MaxwellPowers.NO_POWER
        accessoryPower = 0
        accessories.clear()
        unlockedPowers.clear()
        tunings.clear()
        tuningTemplates.clear()
        save()
    }

    private fun save() = DATA.save()
}
