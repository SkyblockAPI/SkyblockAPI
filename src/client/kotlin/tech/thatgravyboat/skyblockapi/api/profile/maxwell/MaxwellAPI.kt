package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.api.data.stored.MaxwellStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyFound
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findOrNull
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

private const val THAUMATURGY_GUI_ROWS = 5
private const val THAUMATURGY_GUI_LEFT_SPACING = 1
private const val THAUMATURGY_GUI_COLUMNS = 7
private const val THAUMATURGY_GUI_TOP_SPACING = 1

private const val THAUMATURGY_MP_SLOT = 48
private const val THAUMATURGY_STATS_TUNING_SLOT = 51
private val tuningGuiSlots = listOf(19, 20, 21, 22, 28, 29, 30, 31)

private const val BAGS_ACCESSORY_BAG_SLOT = 24

@Suppress("unused")
@Module
object MaxwellAPI {

    val power: MaxwellPower
        get() = MaxwellStorage.power

    val magicalPower: Int
        get() = MaxwellStorage.magicalPower

    val accessories: List<ItemStack>
        get() = MaxwellStorage.accessories

    val unlockedPowers: Set<MaxwellPower>
        get() = MaxwellStorage.unlockedPowers

    val tunings: List<MaxwellTuning>
        get() = MaxwellStorage.tunings

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
        "^Accessory Bag Thaumaturgy$",
    )
    private val selectedPowerRegex = thaumaturgyGuiGroup.create(
        "selected",
        "^Power is selected!",
    )
    private val thaumaturgyMpRegex = thaumaturgyGuiGroup.create(
        "mp",
        "^Total: (?<mp>[\\d,.]+) Magical Power",
    )
    private val thaumaturgyStartTuningRegex = thaumaturgyGuiGroup.create(
        "tuning.start",
        "^Your tuning:"
    )
    private val thaumaturgyTuningRegex = thaumaturgyGuiGroup.create(
        "tuning",
        "(?<amount>[\\d,.]+)(?<icon>.) (?<name>.+)"
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
        "mp",
        "^Magical Power: (?<mp>[\\d,.]+)",
    )
    private val bagsPowerRegex = bagsGroup.create(
        "power",
        "Selected Power: (?<power>.+)",
    )
    private val tuningStartRegex = bagsGroup.create(
        "tuning.start",
        "^Tuning:"
    )

    //region Tunings
    private val tuningsGroup = inventoryGroup.group("tunings")
    private val tuningsTitleRegex = tuningsGroup.create(
        "title",
        "^Stats Tuning$",
    )
    private val tuningsStatRegex = tuningsGroup.create(
        "stat",
        "^(?<icon>.) (?<name>.+)"
    )
    private val tuningsAmountRegex = tuningsGroup.create(
        "amount",
        "^You have: \\S+\\s\\+\\s(?<amount>[\\d,.]+)"
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
    fun onInventoryUpdate(event: ContainerChangeEvent) {
        if (handleThaumaturgyGui(event)) return
        if (handleAccessoryBagGui(event)) return
        if (handleTuningsGui(event)) return
    }

    @OnlyOnSkyBlock
    @Subscription
    fun onInventoryFullyOpened(event: ContainerInitializedEvent) {
        if (handleBagsGui(event)) return
    }

    private fun handleThaumaturgyGui(event: ContainerChangeEvent): Boolean {
        if (!thaumaturgyTitleRegex.contains(event.title)) return false
        val items = event.inventory

        for (row in 0 until THAUMATURGY_GUI_ROWS) {
            for (column in 0 until THAUMATURGY_GUI_COLUMNS) {
                val index = row * (THAUMATURGY_GUI_COLUMNS) + THAUMATURGY_GUI_TOP_SPACING * 9 + column + THAUMATURGY_GUI_LEFT_SPACING
                val itemStack = items[index]
                if (itemStack == ItemStack.EMPTY) continue
                val power = MaxwellPowers.getByName(itemStack.cleanName) ?: continue
                val last = itemStack.getRawLore().lastOrNull() ?: continue
                if (selectedPowerRegex.contains(last)) MaxwellStorage.updatePower(power)
                else MaxwellStorage.addUnlockedPower(power)
            }
        }

        items.getOrNull(THAUMATURGY_MP_SLOT)?.getRawLore()?.lastOrNull()?.let {
            thaumaturgyMpRegex.findOrNull(it, "mp") { (mp) ->
                MaxwellStorage.updateMagicalPower(mp.parseFormattedInt())
            }
        }

        items.getOrNull(THAUMATURGY_STATS_TUNING_SLOT)?.getRawLore()?.let { lore ->
            val tunings = buildList {
                lore.forEach { line ->
                    addIfNotNull(handleTuningsLine(line))
                }
            }
            MaxwellStorage.updateTunings(tunings, false)
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

    private fun handleAccessoryBagGui(event: ContainerChangeEvent): Boolean {
        val match = accessoryBagTitleRegex.find(event.title) ?: return false
        val currentPage = match.groups["current"]?.value?.parseFormattedInt(1) ?: 1
        // TODO: remove player inventory inside ContainerInitializedEvent
        val items = buildList {
            for (stack in event.inventory) {
                if (stack.item == Items.BLACK_STAINED_GLASS_PANE) break
                if (isAccessoryOrEmpty(stack)) add(stack)
            }
        }
        MaxwellStorage.updateAccessories(currentPage, items)

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
                bagsMpRegex.findThenNull(line, "mp") { (mp) ->
                    val newMp = mp.parseFormattedInt()
                    MaxwellStorage.updateMagicalPower(newMp)
                    foundMp = true
                } ?: continue
            }
            if (!foundPower) {
                bagsPowerRegex.findThenNull(line, "power") { (power) ->
                    val newPower = MaxwellPowers.getByName(power) ?: return@findThenNull
                    MaxwellStorage.updatePower(newPower)
                    foundPower = true
                } ?: continue
            }
            if (!foundTunings && !insideTunings && tuningStartRegex.contains(line)) {
                insideTunings = true
                continue
            }
        }

        if (!foundMp) MaxwellStorage.updateMagicalPower(0)
        if (!foundPower) MaxwellStorage.updatePower(MaxwellPowers.NO_POWER)
        MaxwellStorage.updateTunings(tunings, false)
        return true
    }

    private fun handleTuningsGui(event: ContainerChangeEvent): Boolean {
        if (!tuningsTitleRegex.contains(event.title)) return false
        val items = event.inventory
        val tunings = buildList {
            for (slot in tuningGuiSlots) {
                val item = items.getOrNull(slot) ?: continue
                val statName = tuningsStatRegex.findGroup(item.cleanName, "name") ?: continue
                val stat = SkyBlockStat.fromName(statName) ?: continue
                val lore = item.getRawLore()
                tuningsAmountRegex.anyFound(lore, "amount") { (amount) ->
                    val value = amount.parseFormattedDouble()
                    add(MaxwellTuning(stat, value))
                }
            }
        }

        MaxwellStorage.updateTunings(tunings, true)
        return true
    }

    @Subscription
    fun onCommandRegister(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("maxwell") {
                then("reset") {
                    callback {
                        MaxwellStorage.reset()
                        Text.debug("Reset Maxwell Data!").send()
                    }
                }
                then("tunings") {
                    callback {
                        McClient.clipboard = tunings.joinToString { (stat, value) -> "$stat: $value" }
                        Text.debug("Copied tunings to clipboard!").send()
                    }
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
