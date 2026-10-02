package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import tech.thatgravyboat.skyblockapi.api.data.stored.SkillTreeCurrencyStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.addOrPut
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.extentions.toLongValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.matchAll
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.reflect.KClass

private const val MAIN_SLOT = SkillTreeAPI.MAIN_SLOT
private const val PRESET_SLOT = SkillTreeAPI.PRESET_SLOT
private const val RESET_SLOT = SkillTreeAPI.RESET_SLOT

abstract class SkillTreeCurrencyAPI<
    Currency,
    Self : SkillTreeCurrencyAPI<Currency, Self>
> internal constructor(
    val name: String,
    private val storage: SkillTreeCurrencyStorage<Currency>,
    currencyClass: KClass<Currency>,
    val type: SkillTreeType<*>,
    vararg tabWidgets: TabWidget,
) where Currency : SkillTreeCurrency, Currency : Enum<Currency> {

    private val tabWidgets: List<TabWidget> = tabWidgets.toList()
    val currencies: Map<Currency, SkillTreeCurrencyData>
        get() = storage.currencies

    fun getData(currency: Currency): SkillTreeCurrencyData = storage.getOrEmpty(currency)
    fun getCurrent(currency: Currency): Long = storage.getCurrent(currency)
    fun getTotal(currency: Currency): Long = storage.getTotal(currency)

    private val skillTreeApi get() = type.api
    protected open val titleRegex get() = skillTreeApi.titleRegex
    val allCurrencies: List<Currency> = currencyClass.java.enumConstants.toList()

    private val widgetCurrencyRegex = RegexGroup.TABLIST_WIDGET.create(
        "skilltree.$name",
        "\\s*(?<currency>.+): (?<amount>[\\d,.kmb]+)",
    )

    private fun fromWidgetName(name: String) = allCurrencies.find { it.widgetName == name }
    private fun fromInventoryName(name: String) = allCurrencies.find { it.inventoryName == name }

    protected val inventoryGroup = RegexGroup.INVENTORY.group("skilltree.$name")

    protected open val spentCurrencyRegex = inventoryGroup.create(
        "spent",
        "\\s*-\\s*(?<amount>[\\d,.]+) (?<currency>.+)",
    )

    protected open val currentCurrencyRegex = inventoryGroup.create(
        "current",
        "(?<currency>.+): (?<amount>[\\dkmb,.]+)",
    )


    @Subscription(inherited = true)
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        if (event.widget !in tabWidgets) return
        widgetCurrencyRegex.matchAll(event.new, "currency", "amount") { (currency, amount) ->
            val currency = fromWidgetName(currency) ?: return@matchAll
            val amount = amount.parseFormattedLong()
            val diff = amount - storage.getCurrent(currency)
            if (diff <= 0) return@matchAll
            storage.setCurrent(currency, amount)
            storage.addTotal(currency, diff)
        }
    }

    @Subscription(inherited = true)
    @OnlyOnSkyBlock
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (!titleRegex.matches(event.title)) return
        val mainItem = event.itemStacks.getOrNull(MAIN_SLOT) ?: return
        val presetItem = event.itemStacks.getOrNull(PRESET_SLOT)
        val resetItem = event.itemStacks.getOrNull(RESET_SLOT)

        if (presetItem != null) {
            val lore = presetItem.getRawLore()
            val currentPreset = skillTreeApi.getCurrentPreset(lore)
            if (currentPreset != null) storage.swapPreset(currentPreset)
        }

        // We create the map with all the currency entries so that if one of them doesn't appear, it assumes its 0
        val total: MutableMap<Currency, Long> = allCurrencies.associateWithTo(mutableMapOf()) { 0L }
        val mainLore = mainItem.getRawLore()
        currentCurrencyRegex.matchAll(mainLore, "currency", "amount") { (currency, amount) ->
            val currency = fromInventoryName(currency) ?: return@matchAll
            val amount = amount.toLongValue()
            storage.setCurrent(currency, amount)
            total.addOrPut(currency, amount)
        }

        if (resetItem != null) {
            spentCurrencyRegex.matchAll(resetItem.getRawLore(), "currency", "amount") { (currency, amount) ->
                val currency = fromInventoryName(currency) ?: return@matchAll
                val amount = amount.parseFormattedLong()
                total.addOrPut(currency, amount)
                storage.setSpent(currency, amount)
            }
        }

        total.forEach { (currency, total) ->
            storage.setTotal(currency, total)
        }
    }

    @Subscription(inherited = true)
    internal fun onRegisterCommand(event: RegisterSkyblockApiCommandsEvent) {
        event.register(name) {
            thenCallback("all") {
                Text.sendDebug("All $name data:")
                allCurrencies.forEach { currency ->
                    Text.sendDebug("${currency.name}: ${currency.current}/${currency.total}")
                }
            }
            thenCallback("reset") {
                storage.reset()
                Text.sendDebug("Reset $name data!")
            }
        }
    }

}
