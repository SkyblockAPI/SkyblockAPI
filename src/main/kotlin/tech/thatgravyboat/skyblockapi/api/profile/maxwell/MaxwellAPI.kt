package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.api.data.stored.MaxwellStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.IgnoreFiller
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterSkyblockApiCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutAPI.loadoutDebug
import tech.thatgravyboat.skyblockapi.api.profile.items.loadout.LoadoutChangeEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.getSkyBlockId
import tech.thatgravyboat.skyblockapi.api.remote.repo.accessory.RepoAccessoryData
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugAttachable
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.utils.container.ContainerRegion
import tech.thatgravyboat.skyblockapi.utils.devutils.SkyBlockApiDevUtils.debugString
import tech.thatgravyboat.skyblockapi.utils.devutils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.addIfNotNull
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getItemModel
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.extentions.sublistAfterUntil
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyFound
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findOrNull
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull
import tech.thatgravyboat.skyblockapi.utils.regex.findWhen
import tech.thatgravyboat.skyblockapi.utils.text.Text

private const val THAUMATURGY_MP_SLOT = 48
private const val THAUMATURGY_STATS_TUNING_SLOT = 51
private const val BAGS_ACCESSORY_BAG_SLOT = 23
private const val ACCESSORY_BAG_ACCESSORY_BAG_SLOT = 4

private val thaumaturgyPowerStonesRegion = ContainerRegion(width = 7, height = 5, startRow = 1, startColumn = 1)
private val tuningGuiRegion = ContainerRegion(width = 4, height = 2, startRow = 2, startColumn = 1)
private val tuningTemplatesRegion = ContainerRegion(width = 2, height = 4, startRow = 1, startColumn = 6)

@Suppress("unused")
@Module
public data object MaxwellAPI : ItemDebugCategory {

    val power: MaxwellPower
        get() = MaxwellStorage.power

    //? if < 26.3
    //@Deprecated("magicalPower has been renamed by Hypixel", ReplaceWith("accessoryPower")) val magicalPower: Int get() = accessoryPower

    val accessoryPower: Int
        get() = MaxwellStorage.accessoryPower

    //? if < 26.4 {
    /*@Deprecated("Accessories aren't really assigned a specific slot and page anymore.", ReplaceWith("accessories"), level = HIDDEN)
    @Suppress("DEPRECATION")
    val accessoryEntries: Collection<AccessoryBagEntry> get() = emptyList()//?}

    //~ if >= 26.4 'List' -> 'Collection'
    val accessories: List<ItemStack>
        get() = accessoryFamilies.values/*? if < 26.4 */.toList()

    val accessoryFamilies: Map<String, ItemStack>
        get() = MaxwellStorage.accessories

    val unlockedPowers: Set<MaxwellPower>
        get() = MaxwellStorage.unlockedPowers

    val tunings: List<MaxwellTuning>
        get() = MaxwellStorage.tunings.toList()

    val tuningTemplates: Collection<MaxwellTuningTemplate>
        get() = MaxwellStorage.tuningTemplates

    //? if < 26.4 {
    /*@Deprecated("You can no longer consume it.")
    val consumedRiftPrism: Boolean = false*///?}

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
        "^\\((?<current>\\d+)/\\d+\\) Accessory Bag",
    )

    private val bagsGroup = inventoryGroup.group("bags")
    private val bagsTitleRegex = bagsGroup.create(
        "title",
        "^Your Bags$",
    )
    private val bagsApRegex = bagsGroup.create(
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
        "Locked Slot",
    )
    private val lockedTemplateIndexRegex = tuningTemplatesGroup.create(
        "locked.index",
        "Tuning Template #(?<index>[\\d,.]+)",
    )
    private val unlockedTemplateNameIndexRegex = tuningTemplatesGroup.create(
        "index",
        "Tuning Template #(?<index>[\\d,.]+)",
    )
    private val unlockedTemplateStartStatsRegex = tuningTemplatesGroup.create(
        "stats.start",
        "You are loading:",
    )
    //endregion
    //endregion

    private val missingFamiliesToggle = debugToggle("unknown.accessory_family", "Warns when an accessory without an accessory family is found")

    @Subscription
    @OnlyOnSkyBlock
    internal fun onChat(event: ChatReceivedEvent.Pre) {
        findWhen(event.text) {
            case(selectPowerRegex, "power") { (power) ->
                val newPower = MaxwellPowers.getByName(power)
                MaxwellStorage.updatePower(newPower)
            }
        }
    }

    // These need to be on InventoryChangeEvent because you can interact with the GUI and update data
    @Subscription
    @IgnoreFiller
    @OnlyOnSkyBlock
    internal fun onInventoryUpdate(event: InventoryChangeEvent) {
        if (event.isInPlayerInventory) return

        if (handleThaumaturgyGui(event)) return
        if (handleAccessoryBagGui(event)) return
        if (handleTuningsGui(event)) return
    }

    @Subscription
    @OnlyOnSkyBlock
    internal fun onInventoryFullyOpened(event: ContainerInitializedEvent) {
        if (handleBagsGui(event)) return
    }

    @Subscription(priority = Subscription.HIGHEST)
    context(event: LoadoutChangeEvent)
    private fun onLoadoutChange() {
        val newStone = event.new?.powerstone?.value
        if (newStone != null) run {
            val newMaxwellPower = MaxwellPowers.getByName(newStone)
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
                if (item.isEmpty) return true
                val power = MaxwellPowers.getByName(item.cleanName)
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

    private fun handleAccessoryBagGui(event: InventoryChangeEvent): Boolean {
        if (!accessoryBagTitleRegex.containsMatchIn(event.title)) return false

        if (event.slot.index == ACCESSORY_BAG_ACCESSORY_BAG_SLOT) {
            parseAccessoryBagStack(event.item)
            return true
        }
        if (!event.isInMainPart) return true
        val item = event.item
        val itemModel = item.getItemModel()

        // item with "No results" whenever you search
        if (itemModel == Items.BARRIER) return true

        if (itemModel == Items.DYE.gray) {
            val familyName = RepoAccessoryData.getFamilyFromItemName(item.cleanName)?.name
            if (familyName == null) {
                debugString(missingFamiliesToggle) { "Unknown accessory family for accessory ${item.cleanName}" }
                event.addDebugString { "Unknown accessory family" }
                return true
            }
                MaxwellStorage.removeAccessory(familyName)
            event.addDebugString { "No accessory in family $familyName" }

        } else if (isAccessory(item)) {
            val id = item.getSkyBlockId() ?: run {
                debugString(missingFamiliesToggle) { "Accessory without id: ${item.cleanName}" }
                return true
            }
            val family = RepoAccessoryData.getFamilyFromItemId(id)
            val familyName = family?.name ?: run {
                debugString(missingFamiliesToggle) { "Unknown accessory family for accessory ${item.cleanName} [${id.rootId}]" }
                event.addDebugString { "Unknown accessory family for ${id.rootId}, using id as family" }

                return@run id.cleanId
            }
            val tier = family?.tierOf(id)

            MaxwellStorage.updateAccessory(familyName, item)
            event.addDebugString { "Accessory of family $familyName" + if (tier != null) " (tier $tier)" else "" }
        } else {
            debugString(missingFamiliesToggle) { "Unknown item in accessory bag gui: ${item.cleanName}" }
        }
        return true
    }

    private fun handleBagsGui(event: ContainerInitializedEvent): Boolean {
        if (!bagsTitleRegex.contains(event.title)) return false
        val item = event.itemStacks.getOrNull(BAGS_ACCESSORY_BAG_SLOT) ?: return false
        return parseAccessoryBagStack(item)
    }

    private fun parseAccessoryBagStack(item: ItemStack): Boolean {
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
                bagsApRegex.findThenNull(line, "ap") { (ap) ->
                    val newAp = ap.parseFormattedInt()
                    MaxwellStorage.updateAccessoryPower(newAp)
                    item.addDebugString { "Accessory Power: $newAp" }
                    foundMp = true
                } ?: continue
            }
            if (!foundPower) {
                bagsPowerRegex.findThenNull(line, "power") { (power) ->
                    val newPower = MaxwellPowers.getByName(power)
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
                    untilPredicate = String::isBlank,
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
        addDebugString {
            buildString {
                appendLine("Tunings: ")
                tunings.onEach { tuning ->
                    appendLine("  Stat: ${tuning.stat}, Amount: ${tuning.value.toFormattedString()}")
                }.ifEmpty { appendLine("  None") }
            }
        }
    }

    @Subscription(ProfileChangeEvent::class)
    private fun onProfileJoin() = MaxwellStorage.fixAccessoryFamilies()

    @Subscription
    internal fun onCommandRegister(event: RegisterSkyblockApiCommandsEvent) {
        event.register("maxwell") {
            thenCallback("reset") {
                MaxwellStorage.reset()
                Text.sendDebug("Reset Maxwell Data!")
            }
            thenCallback("accessories") {
                val families = mutableMapOf<String, ItemStack?>()
                val unknownFamilies = mutableSetOf<String>()
                val duplicateFamilies = mutableSetOf<String>()

                RepoAccessoryData.families.keys.forEach { families[it] = null }
                MaxwellStorage.accessories.forEach { (family, item) ->
                    if (family !in families) unknownFamilies.add(family)
                    if (families[family] != null) duplicateFamilies.add(family)
                    families[family] = item
                }
                val text = buildString {
                    if (unknownFamilies.isNotEmpty()) {
                        appendLine("Unknown families: ${unknownFamilies.size}")
                    }
                    if (duplicateFamilies.isNotEmpty()) {
                        appendLine("Families with duplicates: ${duplicateFamilies.size}")
                    }
                    appendLine("Owned accessories:")
                    for ((family, item) in families) {
                        append(" - $family: ")
                        appendLine(item?.cleanName ?: "<null>")
                    }
                }
                McClient.clipboard = text
                Text.sendDebug("Copied accessory data to clipboard")
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

    private fun isAccessory(item: ItemStack): Boolean {
        if (item.isEmpty) return false
        val category = item.getData(DataTypes.CATEGORY) ?: return false
        return category.equalsAny(ACCESSORY, HATCESSORY, ignoreModifiers = true)
    }
}
