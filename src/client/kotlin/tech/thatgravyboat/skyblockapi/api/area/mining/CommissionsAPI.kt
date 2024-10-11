package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toFloatValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

data class Commission(val name: String, val area: CommissionArea, var progress: Float)

enum class CommissionArea(val area: String, val areaCheck: () -> Boolean) {
    DWARVEN_MINES("Dwarven Mines", { SkyBlockIsland.DWARVEN_MINES.inIsland() && !GlaciteAPI.inGlaciteTunnels() }),
    CRYSTAL_HOLLOWS("Crystal Hollows", { SkyBlockIsland.CRYSTAL_HOLLOWS.inIsland() }),
    GLACITE_TUNNELS("Glacite Tunnels", { GlaciteAPI.inGlaciteTunnels() }),
    ;

    companion object {

        val currentArea: CommissionArea?
            get() = entries.firstOrNull { it.areaCheck() }

        fun byName(area: String?): CommissionArea? = entries.firstOrNull { it.area == area }
    }
}

@Module
object CommissionsAPI {

    private val inventoryGroup = RegexGroup.INVENTORY.group("commissions")

    private val commissionAreaRegex = inventoryGroup.create("commissionArea", "▶ (?<area>.*)")
    private val commissionItemRegex = inventoryGroup.create("commission", "Commission #\\d+(?: NEW)?")
    private val commissionProgressRegex = inventoryGroup.create("commissionProgress", " *(?<progress>[\\d,.]+)%")
    private val commissionCompletedRegex = inventoryGroup.create("commissionCompleted", "COMPLETED")

    private val tablistGroup = RegexGroup.TABLIST_WIDGET.group("commissions")

    private val commissionTablistRegex = tablistGroup.create("commission", " (?<commission>.*): (?<progress>[\\d,.]+%|DONE)")

    var commissions: List<Commission> = emptyList()
        private set

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

        event.inventory.filter { commissionItemRegex.match(it.hoverName.stripped) }
            .map {
                var progress = 0f
                commissionProgressRegex.anyMatch(it.getRawLore(), "progress") { (percent) ->
                    progress = percent.toFloatValue() / 100
                }
                commissionCompletedRegex.anyMatch(it.getRawLore()) {
                    progress = 1f
                }
                Commission(it.getRawLore().getOrNull(4) ?: "Unknown", commissionArea, progress)
            }
            .let {
                this.commissions = this.commissions.filter { commission -> commission.area != commissionArea } + it
            }
    }

    @Subscription
    @OnlyWidget(TabWidget.COMMISSIONS)
    fun onTabWidgetUpdate(event: TabWidgetChangeEvent) {
        val area = CommissionArea.currentArea
        for (line in event.new) {
            commissionTablistRegex.match(line, "commission", "progress") { (commissionName, progress) ->
                val percent = if (progress == "DONE") 1f else progress.removeSuffix("%").toFloatValue() / 100

                this.commissions.find { it.name == commissionName && it.area == area }?.also {
                    it.progress = percent
                    return@match
                }

                area ?: return@match

                this.commissions += Commission(commissionName, area, percent)
            }
        }
    }

    @Subscription
    fun onProfileSwitch(event: ProfileChangeEvent) {
        this.commissions = emptyList()
    }
}

