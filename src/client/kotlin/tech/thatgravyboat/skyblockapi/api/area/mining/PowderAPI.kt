package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toLongValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object PowderAPI {

    private val powdersGroup = RegexGroup.TABLIST_WIDGET.group("powders")

    private val mithrilPowderRegex = powdersGroup.create("mithril", " Mithril: (?<mithril>[\\d,]+)")
    private val gemstonePowderRegex = powdersGroup.create("gemstone", " Gemstone: (?<gemstone>[\\d,]+)")
    private val glacitePowderRegex = powdersGroup.create("glacite", " Glacite: (?<glacite>[\\d,]+)")

    var mithril: Long = 0
        private set

    var gemstone: Long = 0
        private set

    var glacite: Long = 0
        private set

    @Subscription
    fun onTabWidgetChange(event: TabWidgetChangeEvent) {
        if (event.widget != TabWidget.POWDERS) return

        mithrilPowderRegex.anyMatch(event.new, "mithril") { (mithril) ->
            this.mithril = mithril.toLongValue()
        }

        gemstonePowderRegex.anyMatch(event.new, "gemstone") { (gemstone) ->
            this.gemstone = gemstone.toLongValue()
        }

        glacitePowderRegex.anyMatch(event.new, "glacite") { (glacite) ->
            this.glacite = glacite.toLongValue()
        }
    }

    @Subscription
    fun onDisconnect(event: ServerDisconnectEvent) = reset()

    @Subscription
    fun onSwapProfile(event: ProfileChangeEvent) = reset()

    private fun reset() {
        this.mithril = 0
        this.gemstone = 0
        this.glacite = 0
    }

}
