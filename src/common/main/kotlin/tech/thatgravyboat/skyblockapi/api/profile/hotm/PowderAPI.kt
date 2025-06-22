package tech.thatgravyboat.skyblockapi.api.profile.hotm

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.PowderStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.MustBeContainer
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toLongValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

private const val MAIN_SLOT = 49
private const val RESET_SLOT = 52

@Module
object PowderAPI {

    private val widgetGroup = RegexGroup.TABLIST_WIDGET.group("powders")

    private val mithrilPowderRegex = widgetGroup.create("mithril", " Mithril: (?<mithril>[\\d,]+)")
    private val gemstonePowderRegex = widgetGroup.create("gemstone", " Gemstone: (?<gemstone>[\\d,]+)")
    private val glacitePowderRegex = widgetGroup.create("glacite", " Glacite: (?<glacite>[\\d,]+)")

    private val inventoryGroup = RegexGroup.INVENTORY.group("powders")

    private val mithrilPowderItemRegex = inventoryGroup.create(
        "mithril.current",
        "Mithril Powder: (?<amount>[\\d,.]+)",
    )
    private val gemstonePowderItemRegex = inventoryGroup.create(
        "gemstone.current",
        "Gemstone Powder: (?<amount>[\\d,.]+)",
    )
    private val glacitePowderItemRegex = inventoryGroup.create(
        "glacite.current",
        "Glacite Powder: (?<amount>[\\d,.]+)",
    )

    private val mithrilPowderSpentItemRegex = inventoryGroup.create(
        "mithril.spent",
        "\\s*-\\s*(?<amount>[\\d,.]+) Mithril Powder",
    )
    private val gemstonePowderSpentItemRegex = inventoryGroup.create(
        "gemstone.spent",
        "\\s*-\\s*(?<amount>[\\d,.]+) Gemstone Powder",
    )
    private val glacitePowderSpentItemRegex = inventoryGroup.create(
        "glacite.spent",
        "\\s*-\\s*(?<amount>[\\d,.]+) Glacite Powder",
    )

    var mithril: Long
        get() = PowderStorage.mithrilCurrent
        private set(value) {
            PowderStorage.mithrilCurrent = value
        }

    var gemstone: Long
        get() = PowderStorage.gemstoneCurrent
        private set(value) {
            PowderStorage.gemstoneCurrent = value
        }

    var glacite: Long
        get() = PowderStorage.glaciteCurrent
        private set(value) {
            PowderStorage.glaciteCurrent = value
        }

    var mithrilTotal: Long
        get() = PowderStorage.mithrilTotal
        private set(value) {
            PowderStorage.mithrilTotal = value
        }

    var gemstoneTotal: Long
        get() = PowderStorage.gemstoneTotal
        private set(value) {
            PowderStorage.gemstoneTotal = value
        }

    var glaciteTotal: Long
        get() = PowderStorage.glaciteTotal
        private set(value) {
            PowderStorage.glaciteTotal = value
        }

    @Subscription
    @OnlyWidget(TabWidget.POWDERS)
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        mithrilPowderRegex.anyMatch(event.new, "mithril") { (mithril) ->
            val newMithril = mithril.toLongValue()
            val diff = newMithril - this.mithril
            this.mithril = newMithril
            if (diff > 0) this.mithrilTotal += diff
        }

        gemstonePowderRegex.anyMatch(event.new, "gemstone") { (gemstone) ->
            val newGemstone = gemstone.toLongValue()
            val diff = newGemstone - this.gemstone
            this.gemstone = newGemstone
            if (diff > 0) this.gemstoneTotal += diff
        }

        glacitePowderRegex.anyMatch(event.new, "glacite") { (glacite) ->
            val newGlacite = glacite.toLongValue()
            val diff = newGlacite - this.glacite
            this.glacite = newGlacite
            if (diff > 0) this.glaciteTotal += diff
        }
    }


    @OnlyOnSkyBlock
    @InventoryTitle("Heart of the Mountain")
    @MustBeContainer
    @Subscription
    fun onInventoryChange(event: InventoryChangeEvent) {
        val items = event.itemStacks

        val mainItem = items.getOrNull(MAIN_SLOT) ?: return
        val resetItem = items.getOrNull(RESET_SLOT)

        var mithrilTotal = 0L
        var gemstoneTotal = 0L
        var glaciteTotal = 0L

        val lore = mainItem.getRawLore()
        for (line in lore) {
            mithrilPowderItemRegex.match(line, "amount") { (amount) ->
                this.mithril = amount.toLongValue()
                mithrilTotal += this.mithril
            }
            gemstonePowderItemRegex.match(line, "amount") { (amount) ->
                this.gemstone = amount.toLongValue()
                gemstoneTotal += this.gemstone
            }
            glacitePowderItemRegex.match(line, "amount") { (amount) ->
                this.glacite = amount.toLongValue()
                glaciteTotal += this.glacite
            }
        }

        if (resetItem != null) {
            for (line in resetItem.getRawLore()) {
                mithrilPowderSpentItemRegex.match(line, "amount") { (amount) ->
                    mithrilTotal += amount.toLongValue()
                }
                gemstonePowderSpentItemRegex.match(line, "amount") { (amount) ->
                    gemstoneTotal += amount.toLongValue()
                }
                glacitePowderSpentItemRegex.match(line, "amount") { (amount) ->
                    glaciteTotal += amount.toLongValue()
                }
            }
        }

        this.mithrilTotal = mithrilTotal
        this.gemstoneTotal = gemstoneTotal
        this.glaciteTotal = glaciteTotal
    }

    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("sbapi powder") {
            thenCallback("all") {
                Text.debug("Mithril Powder: ${mithril}/${mithrilTotal}").send()
                Text.debug("Gemstone Powder: ${gemstone}/${gemstoneTotal}").send()
                Text.debug("Glacite Powder: ${glacite}/${glaciteTotal}").send()
            }
            thenCallback("reset") {
                PowderStorage.reset()
                Text.debug("Powder reset.").send()
            }
        }
    }

}
