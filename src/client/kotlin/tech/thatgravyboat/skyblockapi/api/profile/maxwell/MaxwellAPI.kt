package tech.thatgravyboat.skyblockapi.api.profile.maxwell

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockStat
import tech.thatgravyboat.skyblockapi.api.data.stored.MaxwellStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedDouble
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findAll
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findThenNull
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send

private const val THAUMATURGY_GUI_ROWS = 5
private const val THAUMATURGY_GUI_LEFT_SPACING = 1
private const val THAUMATURGY_GUI_COLUMNS = 7
private const val THAUMATURGY_GUI_TOP_SPACING = 1

private const val THAUMATURGY_MP_SLOT = 48
private const val THAUMATURGY_STATS_TUNING_SLOT = 51

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
    private val bagsPowwerRegex = bagsGroup.create(
        "power",
        "Selected Power: (?<power>.+)",
    )

    @Subscription
    fun onChat(event: ChatReceivedEvent) {
        val message = event.text
        selectPowerRegex.findThenNull(message, "power") { (power) ->
            val newPower = MaxwellPowers.getByName(power) ?: return@findThenNull
            MaxwellStorage.updatePower(newPower)
        } ?: return
    }

    @Subscription
    fun onInventoryFullyOpened(event: ContainerInitializedEvent) {
        if (handleThaumaturgyGui(event)) return
        if (handleAccessoryBagGui(event)) return
        if (handleBagsGui(event)) return
    }

    private fun handleThaumaturgyGui(event: ContainerInitializedEvent): Boolean {
        if (!thaumaturgyTitleRegex.matches(event.title)) return false
        val items = event.itemStacks

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
            thaumaturgyMpRegex.findThenNull(it, "mp") { (mp) ->
                MaxwellStorage.updateMagicalPower(mp.parseFormattedInt())
            }
        }

        val tuningsLore = items[THAUMATURGY_STATS_TUNING_SLOT].getRawLore()
        val tunings = buildList {
            thaumaturgyTuningRegex.findAll(tuningsLore, "amount", "name") { (amount, name) ->
                val statName = SkyBlockStat.fromName(name) ?: return@findAll
                val value = amount.parseFormattedDouble()
                add(MaxwellTuning(statName, value))
            }
        }

        MaxwellStorage.updateTunings(tunings, false)

        return true
    }

    private fun handleAccessoryBagGui(event: ContainerInitializedEvent): Boolean {
        val match = accessoryBagTitleRegex.find(event.title) ?: return false
        val currentPage = match.groups["current"]?.value?.parseFormattedInt(1) ?: 1
        // TODO: remove player inventory inside ContainerInitializedEvent
        val items = buildList {
            for (stack in event.itemStacks) {
                if (stack.item == Items.BLACK_STAINED_GLASS_PANE) break
                if (isAccessoryOrEmpty(stack)) add(stack)
            }
        }
        MaxwellStorage.updateAccessories(currentPage, items)

        return true
    }

    private fun handleBagsGui(event: ContainerInitializedEvent): Boolean {
        if (!bagsTitleRegex.matches(event.title)) return false
        val item = event.itemStacks.getOrNull(BAGS_ACCESSORY_BAG_SLOT) ?: return false
        var foundMp = false
        var foundPower = false
        //var foundTunings = false
        for (line in item.getRawLore()) {
            if (foundMp && foundPower/* && foundTunings*/) break
            if (!foundMp) {
                bagsMpRegex.findThenNull(line, "mp") { (mp) ->
                    val newMp = mp.parseFormattedInt()
                    MaxwellStorage.updateMagicalPower(newMp)
                    foundMp = true
                } ?: continue
            }
            if (!foundPower) {
                bagsPowwerRegex.findThenNull(line, "power") { (power) ->
                    val newPower = MaxwellPowers.getByName(power) ?: return@findThenNull
                    MaxwellStorage.updatePower(newPower)
                    foundPower = true
                } ?: continue
            }
            /*if (!foundTunings) {
                // TODO: Implement tunings
            }*/
        }

        if (!foundMp) MaxwellStorage.updateMagicalPower(0)
        if (!foundPower) MaxwellStorage.updatePower(MaxwellPowers.NO_POWER)
        //if (!foundTunings) MaxwellStorage.updateTunings(emptyList())
        return true
    }

    private fun handleTuningsGui(event: ContainerInitializedEvent): Boolean {
        return false
        // TODO: Implement tunings
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
            }
        }
    }

    private fun isAccessoryOrEmpty(item: ItemStack): Boolean {
        if (item == ItemStack.EMPTY) return true
        return when (item.getData(DataTypes.CATEGORY)) {
            SkyBlockCategory.ACCESSORY, SkyBlockCategory.DUNGEON_ACCESSORY,
            SkyBlockCategory.HATCESSORY -> true
            else -> false
        }
    }
}
