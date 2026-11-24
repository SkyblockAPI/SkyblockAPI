package tech.thatgravyboat.skyblockapi.api.profile.skilltree

import tech.thatgravyboat.skyblockapi.api.data.stored.SkillTreeCurrencyStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutSlot
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.StringMatch
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.value
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.utils.extentions.addOrPut
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedLong
import tech.thatgravyboat.skyblockapi.utils.extentions.takeUnlessEmpty
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.extentions.toLongValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.matchAll
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen
import tech.thatgravyboat.skyblockapi.utils.text.Text
import kotlin.reflect.KClass

private const val MAIN_SLOT = SkillTreeAPI.MAIN_SLOT
private const val PRESET_SLOT = SkillTreeAPI.PRESET_SLOT
private const val RESET_SLOT = SkillTreeAPI.RESET_SLOT

public abstract class SkillTreeCurrencyAPI<
    Currency,
    Self : SkillTreeCurrencyAPI<Currency, Self>
> internal constructor(
    public val name: String,
    private val storage: SkillTreeCurrencyStorage<Currency>,
    currencyClass: KClass<Currency>,
    public val type: SkillTreeType<*>,
    vararg tabWidgets: TabWidget,
) : ItemDebugCategory where Currency : SkillTreeCurrency, Currency : Enum<Currency> {

    private val tabWidgets: List<TabWidget> = tabWidgets.toList()
    public val currencies: Map<Currency, SkillTreeCurrencyData>
        get() = storage.currencies

    public fun getData(currency: Currency): SkillTreeCurrencyData = storage.getOrEmpty(currency)
    public fun getCurrent(currency: Currency): Long = storage.getCurrent(currency)
    public fun getTotal(currency: Currency): Long = storage.getTotal(currency)

    private val skillTreeApi get() = type.api
    protected open val titleRegex: Regex get() = skillTreeApi.titleRegex
    protected open val presetsTitleRegex: Regex get() = skillTreeApi.presetsTitleRegex
    public val allCurrencies: List<Currency> = currencyClass.java.enumConstants.toList()

    private val widgetCurrencyRegex = RegexGroup.TABLIST_WIDGET.create(
        "skilltree.$name",
        "\\s*(?<currency>.+): (?<amount>[\\d,.kmb]+)",
    )

    private fun fromWidgetName(name: String) = allCurrencies.find { it.widgetName == name }
    private fun fromInventoryName(name: String) = allCurrencies.find { it.inventoryName == name }

    internal val inventoryGroup = RegexGroup.INVENTORY.group("skilltree.$name")

    protected open val spentCurrencyRegex: Regex = inventoryGroup.create(
        "spent",
        "\\s*-\\s*(?<amount>[\\d,.]+) (?<currency>.+)",
    )

    protected open val currentCurrencyRegex: Regex = inventoryGroup.create(
        "current",
        "(?<currency>.+): (?<amount>[\\dkmb,.]+)",
    )


    @Subscription(inherited = true)
    internal fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        if (event.widget !in tabWidgets) return
        widgetCurrencyRegex.matchAll(event.new, "currency", "amount") { (currency, amount) ->
            val currency = fromWidgetName(currency) ?: return@matchAll
            val amount = amount.parseFormattedLong()
            val diff = amount - storage.getCurrent(currency)
            if (diff <= 0) return@matchAll
            storage.addTotal(currency, diff)
        }
    }

    context(event: InventoryChangeEvent)
    protected open fun onMainInventory() {
        val mainItem = event.itemStacks.getOrNull(MAIN_SLOT).takeUnlessEmpty() ?: return
        val presetItem = event.itemStacks.getOrNull(PRESET_SLOT).takeUnlessEmpty() ?: return
        val resetItem = event.itemStacks.getOrNull(RESET_SLOT).takeUnlessEmpty() ?: return

        val presetLore = presetItem.getRawLore()
        val currentPreset = skillTreeApi.getCurrentPreset(presetLore)
        if (currentPreset != null) {
            storage.currentPreset = currentPreset
        }

        // We create the map with all the currency entries so that if one of them doesn't appear, it assumes its 0
        val total: MutableMap<Currency, Long> = allCurrencies.associateWithTo(mutableMapOf()) { 0L }
        val mainLore = mainItem.getRawLore()
        currentCurrencyRegex.matchAll(mainLore, "currency", "amount") { (currency, amount) ->
            val currency = fromInventoryName(currency) ?: return@matchAll
            val amount = amount.toLongValue()
            total.addOrPut(currency, amount)
        }

        spentCurrencyRegex.matchAll(resetItem.getRawLore(), "currency", "amount") { (currency, amount) ->
            val currency = fromInventoryName(currency) ?: return@matchAll
            val amount = amount.parseFormattedLong()
            total.addOrPut(currency, amount)
            storage.setSpent(currency, amount)
        }

        total.forEach { (currency, total) ->
            storage.setTotal(currency, total)
        }
    }

    context(event: InventoryChangeEvent)
    protected open fun onPresetsInventory() {
        val (id, loadoutName) = skillTreeApi.getLoadoutIdAndName(event, this.fork("Presets Region")) ?: return
        storage.updateLoadout(id, loadoutName)
    }

    @Subscription(inherited = true)
    @OnlyOnSkyBlock
    context(event: InventoryChangeEvent)
    internal fun onInventoryChange() {
        matchWhen(event.title) {
            case(titleRegex) {
                onMainInventory()
            }
            case(presetsTitleRegex) {
                onPresetsInventory()
            }
        }
    }

    @Subscription(inherited = true)
    context(event: LoadoutChangeEvent)
    private fun onLoadoutChange() {
        val newSlot = event.new?.getLoadoutMatch().value() ?: return
        storage.currentPreset = newSlot
    }

    protected abstract fun LoadoutSlot.getLoadoutMatch(): StringMatch?

    @Subscription(inherited = true)
    internal fun onRegisterCommand(event: RegisterSkyblockApiCommandsEvent) {
        event.register(name) {
            thenCallback("all") {
                Text.sendDebug("All $name data:")
                allCurrencies.forEach { currency ->
                    Text.sendDebug("${currency.name}: ${currency.current.toFormattedString()}/${currency.total.toFormattedString()}")
                }
            }
            thenCallback("reset") {
                storage.reset()
                Text.sendDebug("Reset $name data!")
            }
        }
    }

}
