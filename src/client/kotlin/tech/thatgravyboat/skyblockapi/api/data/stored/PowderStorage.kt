package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotm.PowderData

internal object PowderStorage {

    private val POWDER = StoredProfileData<PowderData>("powder.json")

    var mithrilCurrent: Long
        get() = POWDER.get()?.mithril?.current ?: 0L
        set(value) {
            val data = POWDER.get()?.mithril ?: return
            if (data.current == value) return
            data.current = value
            save()
        }

    var gemstoneCurrent: Long
        get() = POWDER.get()?.gemstone?.current ?: 0L
        set(value) {
            val data = POWDER.get()?.gemstone ?: return
            if (data.current == value) return
            data.current = value
            save()
        }

    var glaciteCurrent: Long
        get() = POWDER.get()?.glacite?.current ?: 0L
        set(value) {
            val data = POWDER.get()?.glacite ?: return
            if (data.current == value) return
            data.current = value
            save()
        }

    var mithrilTotal: Long
        get() = POWDER.get()?.mithril?.total ?: 0L
        set(value) {
            val data = POWDER.get()?.mithril ?: return
            if (data.total == value) return
            data.total = value
            save()
        }

    var gemstoneTotal: Long
        get() = POWDER.get()?.gemstone?.total ?: 0L
        set(value) {
            val data = POWDER.get()?.gemstone ?: return
            if (data.total == value) return
            data.total = value
            save()
        }

    var glaciteTotal: Long
        get() = POWDER.get()?.glacite?.total ?: 0L
        set(value) {
            val data = POWDER.get()?.glacite ?: return
            if (data.total == value) return
            data.total = value
            save()
        }

    fun reset() {
        val data = POWDER.get() ?: return
        with(data) {
            mithril.current = 0L
            gemstone.current = 0L
            glacite.current = 0L
            mithril.total = 0L
            gemstone.total = 0L
            glacite.total = 0L
        }
        save()
    }


    private fun save() = POWDER.save()
}
