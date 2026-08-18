package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.data.stored.LoadoutStorage
import tech.thatgravyboat.skyblockapi.api.events.base.SkyBlockEvent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.impl.ColoredItems
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.impl.debug.addStringDebug
import tech.thatgravyboat.skyblockapi.utils.DevUtils
import tech.thatgravyboat.skyblockapi.utils.SkyBlockApiDevUtils
import tech.thatgravyboat.skyblockapi.utils.SkyBlockApiDevUtils.debugMessage
import tech.thatgravyboat.skyblockapi.utils.SkyBlockApiDevUtils.debugString
import tech.thatgravyboat.skyblockapi.utils.container.ContainerRegion
import tech.thatgravyboat.skyblockapi.utils.container.ContentFlow
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import java.util.function.Function
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.floor

@Module
data object LoadoutAPI : ItemDebugCategory {

    internal val loadoutDebug = debugToggle("loadout_debug_messages", "What the name says.")

    private inline val storage get() = LoadoutStorage.loadout

    private val inventoryGroup = RegexGroup.INVENTORY.group("loadout")
    private val chatGroup = RegexGroup.CHAT.group("loadout")
    private val titleRegex = inventoryGroup.create("title", "\\((?<current>\\d+)/(?<max>\\d+)\\) Loadouts")
    private val loadoutEquipped = chatGroup.create("equipped", "You equipped (?<name>.+)!")
    private val armorTypes = listOf("Helmet", "Chestplate", "Leggings", "Boots")
    private val equipmentTypes = listOf("Necklace", "Cloak", "Belt", "Gloves/Bracelet")
    private val containerRegion = ContainerRegion(5..7, 1..4)

    @OptIn(ExperimentalContracts::class)
    private inline fun editLoadout(id: Int, modifier: LoadoutSlot.() -> Unit) {
        contract {
            callsInPlace(modifier, InvocationKind.AT_MOST_ONCE)
        }

        val loadout = storage?.slots?.getOrPut(id) { LoadoutSlot(id) } ?: return
        loadout.modifier()
        LoadoutStorage.save()
    }

    @Subscription
    context(event: InventoryChangeEvent)
    private fun onInventoryUpdate() {
        titleRegex.match(event.title, "current") { (current) ->
            context(DataSource.OVERVIEW) {
                val currentPage = current.toIntValue()
                val slot = containerRegion.getId(event.slot, currentPage, category = fork("Region"), attachable = event) ?: return@match
                event.item.addDebugString { "Slot: $slot" }

                val name = event.item.cleanName
                val locked = event.item.`is`(ColoredItems.RED_DYE)
                event.item.addDebugString { "Locked: $locked" }

                editLoadout(slot) {
                    this.name = name
                    this.locked = locked

                    val armorNames = MutableList<String?>(4) { null }
                    val equipmentNames = MutableList<String?>(4) { null }
                    var hotm: String? = null
                    var hotf: String? = null
                    var powerStone: String? = null
                    var tuningTemplate: Int? = null

                    event.item.getRawLore().forEach {
                        if (armorTypes.any { type -> it.startsWith("$type: ") }) {
                            val index = armorTypes.indexOf(it.substringBefore(':'))
                            val value = it.substringAfter(' ')
                            armorNames[index] = value
                            event.item.addDebugString { "Armor $index -> $value" }
                        }
                        if (equipmentTypes.any { type -> it.startsWith("$type: ") }) {
                            val index = equipmentTypes.indexOf(it.substringBefore(':'))
                            val value = it.substringAfter(' ')
                            equipmentNames[index] = value
                            event.item.addDebugString { "Equipment $index -> $value" }
                        }

                        if (it.startsWith("HOTM: ")) {
                            val value = it.substringAfter(' ').takeUnless { it == "None" }
                            event.item.addDebugString { "HOTM -> $value" }
                            hotm = value
                        }
                        if (it.startsWith("HOTF: ")) {
                            val value = it.substringAfter(' ').takeUnless { it == "None" }
                            event.item.addDebugString { "HOTF -> $value" }
                            hotf = value
                        }
                        if (it.startsWith("Power Stone: ")) {
                            val value = it.substringAfter(':').trim().takeUnless { it == "None" }
                            event.item.addDebugString { "Power Stone -> $value" }
                            powerStone = value
                        }
                        if (it.startsWith("Tuning Template Slot: ")) {
                            val value = it.substringAfter(':').trim().toIntOrNull()
                            event.item.addDebugString { "Tuning Template Slot -> $value" }
                            tuningTemplate = value
                        }
                    }

                    this.armor = value(
                        ArmorWardrobeAPI.slots.indexOfFirst {
                            it.slots.mapIndexed { index, stack ->
                                val name = armorNames[index] ?: return@mapIndexed stack.isEmpty
                                stack.cleanName == name || stack.cleanName.stripColor() == name.stripColor()
                            }.all { it }
                        },
                    )
                    this.equipment = value(
                        EquipmentWardrobeAPI.slots.indexOfFirst {
                            it.slots.mapIndexed { index, stack ->
                                val name = equipmentNames[index] ?: return@mapIndexed stack.isEmpty
                                stack.cleanName == name || stack.cleanName.stripColor() == name.stripColor()
                            }.all { it }
                        },
                    )

                    this.hotm = value(hotm)
                    this.hotf = value(hotf)
                    this.powerstone = value(powerStone)
                    this.tunings = value(tuningTemplate)
                    event.item.addDebugString { "$this" }
                }
            }
        }
    }

    private fun swapLoadout(new: LoadoutSlot) {
        val previous = this.storage?.slots?.get(this.storage?.currentSlot)
        this.storage?.currentSlot = new.id
        LoadoutStorage.save()
        debugString(loadoutDebug) { "Posting event!" }
        LoadoutChangeEvent(previous, new).post()
    }

    @Subscription
    context(event: ChatReceivedEvent.Pre)
    private fun onChat() {
        loadoutEquipped.match(event.text, "name") { (loadout) ->
            val loadout = storage?.slots?.values?.find { it.name == loadout } ?: return@match
            swapLoadout(loadout)
        }
    }
}

data class LoadoutChangeEvent(val old: LoadoutSlot?, val new: LoadoutSlot?) : SkyBlockEvent()

/*
Helmet: Ancient Warden Helmet ✦
Chestplate: ✿ Ancient Fiery Crimson Chestplate ✪✪✪✪
Leggings: ✿ Ancient Fiery Crimson Leggings ✪✪✪✪
Boots: ✿ Ancient Fiery Crimson Boots ✪✪✪✪

Necklace:  Waxed Bone Necklace ✪✪✪✪✪➊
Cloak:  Waxed Shadow Assassin Cloak ✪✪✪✪✪➋
Belt:  Waxed Adaptive Belt ✪✪✪✪✪➋
Gloves/Bracelet: Waxed Soulweaver Gloves ✪✪✪✪✪➋

Pet: [Lvl 100] Black Cat ✦
HOTM: Heart of the Mountain 2
HOTF: Heart of the Forest 1
Power Stone: Bizarre
Tuning Template Slot: 1

Left-click to equip!
Right-click to edit
*/
