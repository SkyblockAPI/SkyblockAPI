package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

data class Commission(val name: String, val area: CommissionArea, var progress: Float)

enum class CommissionArea(val area: String) {
    DWARVEN_MINES("Dwarven Mines"),
    CRYSTAL_HOLLOWS("Crystal Hollows"),
    GLACITE_TUNNELS("Glacite Tunnels"),
    ;

    companion object {
        fun byName(area: String?): CommissionArea? = entries.firstOrNull { it.area == area }
    }
}

@Module
object CommissionsAPI {

    private val inventoryGroup = RegexGroup.INVENTORY.group("commissions")

    private val commissionAreaRegex = inventoryGroup.create("commissionArea", "▶ (?<area>.*)")
    private val commissionItemRegex = inventoryGroup.create("commission", "Commission #\\d+(?: NEW)?")

    private val tablistGroup = RegexGroup.TABLIST_WIDGET.group("commissions")

    private val commissionTablistRegex = tablistGroup.create("commission", " (?<commission>.*): (?<percent>[\\d,.]+)%")

    val commissions = mutableListOf<Commission>()

    @Subscription
    fun onInventoryUpdate(event: ContainerChangeEvent) {
        val commissionAreaStack = event.inventory.firstOrNull { it.hoverName.stripped == "Filter" } ?: return
        val commissionArea = commissionAreaRegex.run {
            var matchedArea: String? = null
            anyMatch(commissionAreaStack.getRawLore(), "area") { (area) ->
                matchedArea = area
            }
            CommissionArea.byName(matchedArea)
        } ?: return

        commissions.removeAll { it.area == commissionArea }

        event.inventory.filter { commissionItemRegex.match(it.hoverName.stripped) }
            .map {
                Commission(it.getRawLore().getOrNull(4) ?: "Unknown", commissionArea, 0f)
            }
            .let { commissions.addAll(it) }
    }

    @Subscription
    fun onTabWidgetUpdate(event: TabWidgetChangeEvent) {
        if (TabWidget.COMMISSIONS != event.widget) return

        for (line in event.new) {
            commissionTablistRegex.match(line, "commission", "percent") { (commissionName, percent) ->
                val progress = percent.toFloatOrNull()?.div(100) ?: 0f

                commissions.find { it.name == commissionName }?.also {
                    it.progress = progress
                }
            }
        }
    }

    @Subscription
    fun onProfileSwitch(event: ProfileChangeEvent) = commissions.clear()
}

