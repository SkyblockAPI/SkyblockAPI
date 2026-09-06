package tech.thatgravyboat.skyblockapi.api.data.stored

import com.mojang.serialization.Codec
import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrency
import tech.thatgravyboat.skyblockapi.api.profile.skilltree.SkillTreeCurrencyData
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs
import java.util.EnumMap
import kotlin.reflect.KClass

internal abstract class SkillTreeCurrencyStorage<Currency>(
    fileName: String,
    clazz: KClass<Currency>,
) where Currency : SkillTreeCurrency, Currency : Enum<Currency> {

    @Suppress("UNCHECKED_CAST")
    protected open val STORAGE: StoredProfileData<EnumMap<Currency, SkillTreeCurrencyData>> = StoredProfileData(
        { EnumMap(clazz.java) },
        CodecUtils.enumMap(clazz.java, SkyblockAPICodecs.getCodec(clazz.java) as Codec<Currency>, SkyblockAPICodecs.getCodec<SkillTreeCurrencyData>()),
        fileName,
    )

    private inline val data get() = STORAGE.get()

    val currencies: Map<Currency, SkillTreeCurrencyData>
        get() = data.orEmpty()

    @Suppress("PrivatePropertyName")
    private val EMPTY = SkillTreeCurrencyData()

    fun getOrEmpty(currency: Currency): SkillTreeCurrencyData = data?.get(currency) ?: EMPTY
    fun getCurrent(currency: Currency): Long = getOrEmpty(currency).current
    fun getTotal(currency: Currency): Long = getOrEmpty(currency).total

    fun setCurrent(currency: Currency, amount: Long) = STORAGE.edit {
        val data = get(currency)
        if (data == null) {
            this[currency] = SkillTreeCurrencyData(amount)
        } else {
            if (data.current == amount) return@edit null
            this[currency] = data.copy(current = amount)
        }
    }

    fun setTotal(currency: Currency, amount: Long) = STORAGE.edit {
        val data = get(currency)
        if (data == null) {
            this[currency] = SkillTreeCurrencyData(0, amount)
        } else {
            if (data.total == amount) return@edit null
            this[currency] = data.copy(total = amount)
        }
    }

    fun addTotal(currency: Currency, amount: Long) {
        if (amount == 0L) return
        val data = STORAGE.get() ?: return
        val currencyData = data.getOrElse(currency, ::SkillTreeCurrencyData)
        data[currency] = currencyData.copy(total = currencyData.total + amount)
        save()
    }

    fun reset() = STORAGE.edit { clear() }

    private fun save() = STORAGE.save()

}
