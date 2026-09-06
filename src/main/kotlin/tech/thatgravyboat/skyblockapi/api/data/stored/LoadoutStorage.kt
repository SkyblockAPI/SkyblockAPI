package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.Loadout
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutData
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutSlot
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeData
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.WardrobeSlot

internal object LoadoutStorage {
    private val LOADOUT = StoredProfileData(
        ::LoadoutData,
        LoadoutData.CODEC,
        "loadout.json",
    )

    var armor: WardrobeData?
        get() = LOADOUT.get()?.armor
        private set(value) {
            LOADOUT.get()?.armor = value ?: WardrobeData()
        }

    var equipment: WardrobeData?
        get() = LOADOUT.get()?.equipment
        private set(value) {
            LOADOUT.get()?.equipment = value ?: WardrobeData()
        }

    var loadout: Loadout?
        get() = LOADOUT.get()?.loadouts
        private set(value) {
            LOADOUT.get()?.loadouts = value ?: Loadout()
        }


    fun updateCurrentArmorSlot(slot: Int?) {
        if (slot == armor?.currentSlot) return
        armor?.currentSlot = slot ?: -1
        LOADOUT.save()
    }

    fun updateCurrentEquipmentSlot(slot: Int?) {
        if (slot == equipment?.currentSlot) return
        equipment?.currentSlot = slot ?: -1
        LOADOUT.save()
    }

    fun updateCurrentLoadoutSlot(slot: Int?) {
        if (slot == loadout?.currentSlot) return
        loadout?.currentSlot = slot ?: -1
        LOADOUT.save()
    }

    fun updateArmorSlot(wardrobeSlot: WardrobeSlot) {
        armor?.slots = armor?.slots?.filter { it.id != wardrobeSlot.id }?.toMutableList()?.apply { add(wardrobeSlot) } ?: mutableListOf()
        armor?.slots?.sortBy { it.id }
        LOADOUT.save()
    }

    fun updateEquipmentSlot(wardrobeSlot: WardrobeSlot) {
        equipment?.slots = equipment?.slots?.filter { it.id != wardrobeSlot.id }?.toMutableList()?.apply { add(wardrobeSlot) } ?: mutableListOf()
        equipment?.slots?.sortBy { it.id }
        LOADOUT.save()
    }

    fun updateLoadoutSlot(loadoutSlot: LoadoutSlot) {
        loadout?.slots?.put(loadoutSlot.id, loadoutSlot)
        LOADOUT.save()
    }


    fun clearArmor() {
        armor = null
        LOADOUT.save()
    }

    fun clearEquipment() {
        equipment = null
        LOADOUT.save()
    }

    fun clearLoadouts() {
        loadout = null
        LOADOUT.save()
    }

    fun save() = LOADOUT.save()
}
