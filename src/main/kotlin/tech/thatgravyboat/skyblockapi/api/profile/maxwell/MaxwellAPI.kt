package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.api.data.stored.MaxwellStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.IgnoreFiller
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutAPI.loadoutDebug
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugAttachable
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.utils.SkyBlockApiDevUtils.debugString
import tech.thatgravyboat.skyblockapi.utils.container.ContainerRegion
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyFound
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findOrNull
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.indexOfFirstMatch
import tech.thatgravyboat.skyblockapi.utils.text.Text

private const val THAUMATURGY_MP_SLOT = 48
private const val THAUMATURGY_STATS_TUNING_SLOT = 51
private const val BAGS_ACCESSORY_BAG_SLOT = 24

private val thaumaturgyPowerStonesRegion = ContainerRegion(width = 7, height = 5, startRow = 1, startColumn = 1)
private val tuningGuiRegion = ContainerRegion(width = 4, height = 2, startRow = 2, startColumn = 1)
private val tuningTemplatesRegion = ContainerRegion(width = 2, height = 4, startRow = 1, startColumn = 6)

@Suppress("unused")
@Module
data object MaxwellAPI : ItemDebugCategory {

    val power: MaxwellPower
        get() = MaxwellStorage.power

    //? < 26.3
    //@Deprecated("magicalPower has been renamed by Hypixel", ReplaceWith("accessoryPower")) val magicalPower: Int get() = accessoryPower

    val accessoryPower: Int
        get() = MaxwellStorage.accessoryPower

    val accessories: List<ItemStack>
        get() = MaxwellStorage.accessories

    val unlockedPowers: Set<MaxwellPower>
        get() = MaxwellStorage.unlockedPowers

    val tunings: List<MaxwellTuning>
        get() = MaxwellStorage.tunings.toList()

    val tuningTemplates: Collection<MaxwellTuningTemplate>
        get() = MaxwellStorage.tuningTemplates

    //region Regex
    private val chatGroup = RegexGroup.CHAT.group("maxwell")

    private val selectPowerRegex = chatGroup.create(
        "select",
        "^(?:Your selected power was set to |You selected the )(?<power>.+?)(?:!|(?: power)? for your Accessory Bag!)",
    )

    private val inventoryGroup = RegexGroup.INVENTORY.group("maxwell")

    //region Thaumaturgy
    private val thaumaturgyGuiGroup = inventoryGroup.group("thaumaturgy")
    private val thaumaturgyTitleRegex = thaumaturgyGuiGroup.create(
        "title",
        "^(?:\\((?<currentPage>\\d+)/\\d+\\) )?Accessory Bag Thaumaturgy$",
    )
    private val selectedPowerRegex = thaumaturgyGuiGroup.create(
        "selected",
        "^Power is selected!",
    )
    private val thaumaturgyApRegex = thaumaturgyGuiGroup.create(
        "ap",
        "^Total: (?<ap>[\\d,.]+) Accessory Power",
    )
    private val thaumaturgyStartTuningRegex = thaumaturgyGuiGroup.create(
        "tuning.start",
        "^Your tuning:",
    )
    private val thaumaturgyTuningRegex = thaumaturgyGuiGroup.create(
        "tuning",
        "(?<amount>[\\d,.]+)(?<icon>.) (?<name>.+)",
    )
    //endregion

    private val accessoryBagTitleRegex = inventoryGroup.create(
        "accessory_bag.title",
        "^Accessory Bag(?: \\((?<current>[\\d,.]+))?",
    )

    private val bagsGroup = inventoryGroup.group("bags")
    private val bagsTitleRegex = bagsGroup.create(
        "title",
        "^Your Bags$",
    )
    private val bagsMpRegex = bagsGroup.create(
        "ap",
        "^Accessory Power: (?<ap>[\\d,.]+)",
    )
    private val bagsPowerRegex = bagsGroup.create(
        "power",
        "Selected Power: (?<power>.+)",
    )
    private val tuningStartRegex = bagsGroup.create(
        "tuning.start",
        "^Tuning:",
    )

    //region Tunings
    private val tuningsGroup = inventoryGroup.group("tunings")
    private val tuningsTitleRegex = tuningsGroup.create(
        "title",
        "^Stats Tuning$",
    )
    private val tuningsStatRegex = tuningsGroup.create(
        "stat",
        "^(?<icon>.) (?<name>.+)",
    )
    private val tuningsAmountRegex = tuningsGroup.create(
        "amount",
        "^You have: \\S+\\s\\+\\s(?<amount>[\\d,.]+)",
    )

    private val tuningTemplatesGroup = tuningsGroup.group("templates")
    private val lockedTemplateNameRegex = tuningTemplatesGroup.create(
        "locked.name",
        "Locked Slot", // TODO: not correct
    )
    private val lockedTemplateIndexRegex = tuningTemplatesGroup.create(
        "locked.index",
        "Tuning Template #(?<index>[\\d,.]+)", // TODO: not correct
    )
    private val unlockedTemplateNameIndexRegex = tuningTemplatesGroup.create(
        "index",
        "Tuning Template #(?<index>[\\d,.]+)", // TODO: not correct
    )
    private val unlockedTemplateStartStatsRegex = tuningTemplatesGroup.create(
        "stats.start",
        "You are loading:", // TODO: get correct thingy
    )
    //endregion
    //endregion

    @OnlyOnSkyBlock
    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        val message = event.text
        selectPowerRegex.findThenNull(message, "power") { (power) ->
            val newPower = MaxwellPowers.getByName(power) ?: return@findThenNull
            MaxwellStorage.updatePower(newPower)
        } ?: return
    }

    // These need to be on ContainerChangeEvent because you can interact with the GUI and update data
    @OnlyOnSkyBlock
    @Subscription
    fun onInventoryUpdate(event: InventoryChangeEvent) {
        if (event.isInPlayerInventory) return

        if (handleThaumaturgyGui(event)) return
        if (handleAccessoryBagGui(event)) return
        if (handleTuningsGui(event)) return
    }

    @OnlyOnSkyBlock
    @Subscription
    fun onInventoryFullyOpened(event: ContainerInitializedEvent) {
        if (handleBagsGui(event)) return
    }

    @Subscription(ServerChangeEvent::class, ServerDisconnectEvent::class)
    fun onServerChange() = MaxwellStorage.fixEmptyAccessories()

    @Subscription(priority = Subscription.HIGHEST)
    context(event: LoadoutChangeEvent)
    private fun onLoadoutChange() {
        val newStone = event.new?.powerstone?.value
        if (newStone != null) run {
            val newMaxwellPower = MaxwellPowers.getByName(newStone)
            if (newMaxwellPower == null) {
                debugString(loadoutDebug) { "Unknown power stone $newStone" }
                return@run
            }
            MaxwellStorage.updatePower(newMaxwellPower)
            debugString(loadoutDebug) { "Set power stone to ${newMaxwellPower.internalName}" }
        }

        val newTuningTemplate = event.new?.tunings?.value
        if (newTuningTemplate != null) run {
            val newTemplate = tuningTemplates.find { it.index == newTuningTemplate }
            if (newTemplate == null) {
                debugString(loadoutDebug) { "Unknown tuning template $newTemplate" }
                return@run
            }
            MaxwellStorage.updateTunings(newTemplate.tunings)
            debugString(loadoutDebug) {
                "Set new tunings to template #$newTuningTemplate: ${newTemplate.tunings}"
            }
        }
    }

    private fun handleThaumaturgyGui(event: InventoryChangeEvent): Boolean {
        if (!thaumaturgyTitleRegex.contains(event.title)) return false
        val item = event.item
        when (val index = event.slot.index) {
            THAUMATURGY_MP_SLOT -> {
                val lastLoreLine = event.item.getRawLore().lastOrNull() ?: return true
                thaumaturgyApRegex.findOrNull(lastLoreLine, "ap") { (apString) ->
                    val ap = apString.parseFormattedInt()
                    event.addDebugString { "Accessory Power: $ap" }
                    MaxwellStorage.updateAccessoryPower(ap)
                }
            }
            THAUMATURGY_STATS_TUNING_SLOT -> {
                val tunings = buildList {
                    item.getRawLore().forEach { line ->
                        addIfNotNull(handleTuningsLine(line))
                    }
                }
                event.appendTuningDebug(tunings)
                MaxwellStorage.updateTunings(tunings)
            }
            in thaumaturgyPowerStonesRegion -> {
                if (item == ItemStack.EMPTY) return true
                val power = MaxwellPowers.getByName(item.cleanName) ?: return true
                val last = item.getRawLore().lastOrNull() ?: return true
                val isSelected = selectedPowerRegex.contains(last)
                if (isSelected) MaxwellStorage.updatePower(power)
                else MaxwellStorage.addUnlockedPower(power)
                event.addDebugString { "Maxwell power: ${power.internalName}, selected: $isSelected" }
            }
        }
        return true
    }

    private fun handleTuningsLine(line: String): MaxwellTuning? {
        return thaumaturgyTuningRegex.findOrNull(line, "amount", "name") { (amount, name) ->
            val statName = SkyBlockStat.fromName(name) ?: return@findOrNull null
            val value = amount.parseFormattedDouble()
            return@findOrNull MaxwellTuning(statName, value)
        }
    }

    // TODO: maybe use ContainerRegion.getId for getting the "real" index?
    private fun handleAccessoryBagGui(event: InventoryChangeEvent): Boolean {
        val match = accessoryBagTitleRegex.find(event.title) ?: return false
        if (isAccessoryOrEmpty(event.item)) {
            val currentPage = match.groups["current"]?.value?.parseFormattedInt(1) ?: 1
            MaxwellStorage.updateAccessory(currentPage, event.slot.index, event.item)
            event.addDebugString { "Accessory in page $currentPage" }
        }
        return true
    }

    private fun handleBagsGui(event: ContainerInitializedEvent): Boolean {
        if (!bagsTitleRegex.contains(event.title)) return false
        val item = event.itemStacks.getOrNull(BAGS_ACCESSORY_BAG_SLOT) ?: return false
        var foundMp = false
        var foundPower = false
        var foundTunings = false
        var insideTunings = false
        val tunings = mutableListOf<MaxwellTuning>()

        for (line in item.getRawLore()) {
            if (foundMp && foundPower && foundTunings) break
            if (insideTunings) {
                tunings.addIfNotNull(handleTuningsLine(line))
            }
            if (insideTunings && line.isEmpty()) {
                insideTunings = false
                foundTunings = true
                continue
            }
            if (!foundMp) {
                bagsMpRegex.findThenNull(line, "ap") { (ap) ->
                    val newAp = ap.parseFormattedInt()
                    MaxwellStorage.updateAccessoryPower(newAp)
                    item.addDebugString { "Accessory Power: $newAp" }
                    foundMp = true
                } ?: continue
            }
            if (!foundPower) {
                bagsPowerRegex.findThenNull(line, "power") { (power) ->
                    val newPower = MaxwellPowers.getByName(power) ?: return@findThenNull
                    MaxwellStorage.updatePower(newPower)
                    item.addDebugString { "Maxwell Power: ${newPower.internalName}" }
                    foundPower = true
                } ?: continue
            }
            if (!foundTunings && !insideTunings && tuningStartRegex.contains(line)) {
                insideTunings = true
                continue
            }
        }

        if (!foundMp) {
            MaxwellStorage.updateAccessoryPower(0)
            item.addDebugString { "Accessory Power: 0" }
        }
        if (!foundPower) {
            MaxwellStorage.updatePower(MaxwellPowers.NO_POWER)
            item.addDebugString { "Maxwell Power: NO_POWER" }
        }
        MaxwellStorage.updateTunings(tunings)
        item.appendTuningDebug(tunings)
        return true
    }

    private fun handleTuningsGui(event: InventoryChangeEvent): Boolean {
        if (!tuningsTitleRegex.contains(event.title)) return false
        val item = event.item

        when (val slot = event.slot) {
            in tuningGuiRegion -> {
                val statName = tuningsStatRegex.findGroup(item.cleanName, "name") ?: return true
                val stat = SkyBlockStat.fromName(statName) ?: run {
                    event.addDebugString { "Unknown stat name: $statName" }
                    return true
                }
                event.addDebugString { "Tuning stat: $stat" }
                val hasAmount = tuningsAmountRegex.anyFound(item.getRawLore(), "amount") { (amount) ->
                    val value = amount.parseFormattedDouble()
                    event.addDebugString { "Tuning Amount: ${value.toFormattedString()}" }
                    val tuning = MaxwellTuning(stat, value)
                    MaxwellStorage.setTuning(tuning)
                }
                if (!hasAmount) {
                    event.addDebugString { "Tuning Amount: 0" }
                    MaxwellStorage.removeTuning(stat)
                }
            }
            in tuningTemplatesRegion -> {
                val isLocked = lockedTemplateNameRegex.matches(item.cleanName)
                if (isLocked) {
                    val index = lockedTemplateIndexRegex.findGroup(item.getRawLore(), "index")?.parseFormattedInt() ?: return true
                    MaxwellStorage.lockTuningTemplateSlot(index)
                    event.addDebugString { "Locked Tuning Template (Index: $index)" }
                    return true
                }
                val index = unlockedTemplateNameIndexRegex.findGroup(item.cleanName, "index")?.parseFormattedInt() ?: return true
                val lore = item.getRawLore()
                val sublist = lore.sublistAfterUntil(
                    beforePredicate = unlockedTemplateStartStatsRegex::matches,
                    untilPredicate = String::isBlank
                )
                val tunings: Set<MaxwellTuning> = sublist.mapNotNullTo(mutableSetOf()) { line ->
                    handleTuningsLine(line)
                }
                event.addDebugString { "Tuning slot #$index" }
                event.appendTuningDebug(tunings)
                MaxwellStorage.updateTuningTemplateSlot(index, tunings)
            }
        }
        return true
    }

    private fun ItemDebugAttachable.appendTuningDebug(tunings: Collection<MaxwellTuning>) {
        addDebugString { buildString {
            appendLine("Tunings: ")
            tunings.onEach { tuning ->
                appendLine("  Stat: ${tuning.stat}, Amount: ${tuning.value.toFormattedString()}")
            }.ifEmpty { appendLine("  None") }
        }}
    }

    @Subscription
    fun onCommandRegister(event: RegisterCommandsEvent) {
        event.register("sbapi maxwell") {
            thenCallback("reset") {
                MaxwellStorage.reset()
                Text.sendDebug("Reset Maxwell Data!")
            }
            then("tunings") {
                thenCallback("templates") {
                    McClient.clipboard = tuningTemplates.joinToString("\n") { (index, locked, tunings) ->
                        buildString {
                            appendLine("Template: #$index")
                            if (locked) appendLine("  Locked")
                            else appendLine(tunings.joinToString { (stat, value) -> "  $stat: $value" })
                        }
                    }
                    Text.sendDebug("Copied tuning templates to clipboard!")
                }
                callback {
                    McClient.clipboard = tunings.joinToString { (stat, value) -> "$stat: $value" }
                    Text.sendDebug("Copied tunings to clipboard!")
                }
            }
        }
    }

    private fun isAccessoryOrEmpty(item: ItemStack): Boolean {
        if (item.isEmpty) return true
        val category = item.getData(DataTypes.CATEGORY) ?: return false
        return category.equalsAny(SkyBlockCategory.ACCESSORY, SkyBlockCategory.HATCESSORY, ignoreDungeon = true)
    }
}
