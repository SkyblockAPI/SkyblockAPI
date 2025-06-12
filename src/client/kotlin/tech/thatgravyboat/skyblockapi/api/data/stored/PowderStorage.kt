package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.hotm.PowderData

internal object PowderStorage {

    private val POWDER = StoredProfileData<PowderData>("powder.json")

    var mithrilCurrent: Long
        get() = POWDER.get()?.mithril?.current ?: 0L
        set(value) {
            val data = POWDER.get() ?: return
            data.mithril.current = value
            save()
        }

    var gemstoneCurrent: Long
        get() = POWDER.get()?.gemstone?.current ?: 0L
        set(value) {
            val data = POWDER.get() ?: return
            data.gemstone.current = value
            save()
        }

    var glaciteCurrent: Long
        get() = POWDER.get()?.glacite?.current ?: 0L
        set(value) {
            val data = POWDER.get() ?: return
            data.glacite.current = value
            save()
        }

    var mithrilTotal: Long
        get() = POWDER.get()?.mithril?.total ?: 0L
        set(value) {
            val data = POWDER.get() ?: return
            data.mithril.total = value
            save()
        }

    var gemstoneTotal: Long
        get() = POWDER.get()?.gemstone?.total ?: 0L
        set(value) {
            val data = POWDER.get() ?: return
            data.gemstone.total = value
            save()
        }

    var glaciteTotal: Long
        get() = POWDER.get()?.glacite?.total ?: 0L
        set(value) {
            val data = POWDER.get() ?: return
            data.glacite.total = value
            save()
        }


    private fun save() = POWDER.save()
}
