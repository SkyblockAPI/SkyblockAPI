package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyData
import tech.thatgravyboat.skyblockapi.api.profile.currency.PurseType

internal object CurrencyStorage {

    private val CURRENCY = StoredProfileData<CurrencyData>("currency.json")
    private val data: CurrencyData? get() = CURRENCY.get()

    var purse: Double
        get() = data?.purse ?: 0.0
        set(value) {
            CURRENCY.edit {
                if (purse == value) return
                purse = value
            }
        }

    var purseType: PurseType
        get() = data?.purseType ?: PurseType.UNKNOWN
        set(value) {
            CURRENCY.edit {
                if (purseType == value) return
                purseType = value
            }
        }

    var personalBank: Long
        get() = data?.personalBank ?: 0L
        set(value) {
            CURRENCY.edit {
                if (personalBank == value) return
                personalBank = value
            }
        }

    var coopBank: Long
        get() = data?.coopBank ?: 0L
        set(value) {
            CURRENCY.edit {
                if (coopBank == value) return
                coopBank = value
            }
        }

    var motes: Long
        get() = data?.motes ?: 0L
        set(value) {
            CURRENCY.edit {
                if (motes == value) return
                motes = value
            }
        }

    var bits: Long
        get() = data?.bits ?: 0L
        set(value) {
            CURRENCY.edit {
                if (bits == value) return
                bits = value
            }
        }

    var copper: Long
        get() = data?.copper ?: 0L
        set(value) {
            CURRENCY.edit {
                if (copper == value) return
                copper = value
            }
        }

    var sowdust: Long
        get() = data?.sowdust ?: 0L
        set(value) {
            CURRENCY.edit {
                if (sowdust == value) return
                sowdust = value
            }
        }

    var kernels: Long
        get() = data?.kernels ?: 0L
        set(value) {
            CURRENCY.edit {
                if (kernels == value) return
                kernels = value
            }
        }

    var northStars: Long
        get() = data?.northStars ?: 0L
        set(value) {
            CURRENCY.edit {
                if (northStars == value) return
                northStars = value
            }
        }

    var soulflow: Long
        get() = data?.soulflow ?: 0L
        set(value) {
            CURRENCY.edit {
                if (soulflow == value) return
                soulflow = value
            }
        }

}
