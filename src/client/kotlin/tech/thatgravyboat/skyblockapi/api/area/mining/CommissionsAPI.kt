package tech.thatgravyboat.skyblockapi.api.area.mining

import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

data class Commission(val name: String, val area: CommissionArea)

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
    private val commissionRegex = inventoryGroup.create("commission", "Commission #\\d+(?: NEW)?")

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

        event.inventory.filter { commissionRegex.match(it.hoverName.stripped) }
            .map { Commission(it.getRawLore().getOrNull(4) ?: "Unknown", commissionArea) }
            .let { commissions.addAll(it) }
    }

    @Subscription
    fun onProfileSwitch(event: ProfileChangeEvent) = commissions.clear()
}

