package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillCurrencyData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrency
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrencyData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrencyLoadout
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import tech.thatgravyboat.skyblockapi.utils.extentions.addOrPut
import tech.thatgravyboat.skyblockapi.utils.extentions.changeValue
import tech.thatgravyboat.skyblockapi.utils.extentions.kClass
import kotlin.reflect.KType

internal abstract class SkillTreeCurrencyStorage<Currency>(
    fileName: String,
    kType: KType,
) where Currency : SkillTreeCurrency, Currency : Enum<Currency> {

    protected open val storage = StoredProfileData<SkillCurrencyData<Currency>>(
        file = fileName,
        codec = SkyblockAPICodecs.SkillCurrencyDataCodec<Currency>(kType).codec(),
    )

    private inline val data get() = storage.get()

    fun getLoadoutByName(name: String?): SkillTreeCurrencyLoadout<Currency>? {
        if (name == null) return null
        val data = data ?: return null

        val current = data.loadouts.find { it.name == name }
        if (current != null) return current

        val new = SkillTreeCurrencyLoadout<Currency>(name)
        data.loadouts.add(new)
        save()

        return new
    }

    val currentLoadout: SkillTreeCurrencyLoadout<Currency>?
        get() = getLoadoutByName(currentPreset)

    var currentPreset: String?
        get() = storage.get()?.currentLoadoutName
        set(value) = storage.edit {
            if (currentLoadoutName == value) return
            currentLoadoutName = value
        }

    val currencies: Map<Currency, SkillTreeCurrencyData>
        get() {
            val data = data ?: return emptyMap()
            val current = currentLoadout ?: return emptyMap()
            return allCurrencies.associateWith { currency ->
                val spent = current.currencySpent[currency] ?: 0L
                val total = data.total[currency] ?: 0L
                SkillTreeCurrencyData(current = total - spent, total = total)
            }
        }

    @Suppress("PrivatePropertyName")
    private val EMPTY = SkillTreeCurrencyData()

    fun getOrEmpty(currency: Currency): SkillTreeCurrencyData = currencies[currency] ?: EMPTY
    fun getCurrent(currency: Currency): Long = getOrEmpty(currency).current
    fun getTotal(currency: Currency): Long = getOrEmpty(currency).total
    fun getSpent(currency: Currency): Long = getOrEmpty(currency).spent

    fun setSpent(currency: Currency, amount: Long) {
        val current = currentLoadout ?: return
        if (!current.currencySpent.changeValue(currency, amount)) return
        save()
    }

    fun setTotal(currency: Currency, amount: Long) {
        val data = data ?: return
        if (!data.total.changeValue(currency, amount)) return
        save()
    }

    fun addTotal(currency: Currency, amount: Long) {
        if (amount == 0L) return
        val data = data ?: return
        data.total.addOrPut(currency, amount)
        save()
    }

    fun reset() = storage.deleteCurrent()

    private fun save() = storage.save()

    @Suppress("UNCHECKED_CAST")
    val allCurrencies: List<Currency> = kType.kClass!!.java.enumConstants.toList() as List<Currency>

}
