package tech.thatgravyboat.skyblockapi.api.profile.items.loadout

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.stored.LoadoutStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.profile.ProfileChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerCloseEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.ColoredItems
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.utils.extentions.roundToNextMultipleOf
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import kotlin.math.max

private const val SELECT_START_INDEX = 36
private const val WARDROBE_SLOTS_PER_PAGE = 9

@Module
object ArmorWardrobeAPI {
    private val wardrobeGroup = RegexGroup.INVENTORY.group("wardrobe.armor")

    private val inventoryNameRegex = wardrobeGroup.create(
        "title",
        "\\((?<currentPage>\\d+)/\\d+\\) Armor Sets",
    )

    private val equippedRegex = wardrobeGroup.create(
        "equip",
        "Slot \\d+: Equipped",
    )

    private val emptyArmor = mutableListOf(ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY)

    var inWardrobe = false
        private set

    /** 0 if not in wardrobe */
    var currentPage = 0
        private set

    val slots get() = LoadoutStorage.armor?.slots ?: emptyList()
    val currentSlot: Int? get() = LoadoutStorage.armor?.currentSlot


    private fun processInventory(title: String, items: List<ItemStack>) {
        inventoryNameRegex.match(title, "currentPage") { (currentPage) ->
            currentPage.toIntOrNull()?.let { this.currentPage = it }
        }

        var foundCurrentSlot = false

        for (index in 0..<WARDROBE_SLOTS_PER_PAGE) {
            val selectStack = items[index + SELECT_START_INDEX]
            val id = WARDROBE_SLOTS_PER_PAGE * (currentPage - 1) + index + 1
            var locked = false

            if (selectStack.item == ColoredItems.RED_DYE) {
                locked = true
            } else if (equippedRegex.match(selectStack.hoverName.stripped)) {
                LoadoutStorage.updateCurrentArmorSlot(id)
                foundCurrentSlot = true
            }

            val helmetStack = items[index].takeOrEmpty()
            val chestplateStack = items[index + 9].takeOrEmpty()
            val leggingsStack = items[index + 18].takeOrEmpty()
            val bootsStack = items[index + 27].takeOrEmpty()

            val slot = WardrobeSlot(id, mutableListOf(helmetStack, chestplateStack, leggingsStack, bootsStack), locked)

            LoadoutStorage.updateArmorSlot(slot)
        }

        if (!foundCurrentSlot && isCurrentSlotInCurrentPage()) {
            LoadoutStorage.updateCurrentArmorSlot(null)
        }
    }

    fun isCurrentSlotInCurrentPage(): Boolean {
        val slot = currentSlot ?: return false
        val first = (currentPage - 1) * WARDROBE_SLOTS_PER_PAGE + 1
        val last = first + WARDROBE_SLOTS_PER_PAGE - 1
        return slot in first..last
    }

    @Subscription
    fun onInventoryUpdate(event: InventoryChangeEvent) {
        inWardrobe = inventoryNameRegex.matches(event.title)

        if (inWardrobe) processInventory(event.title, event.itemStacks)
    }

    @Subscription
    fun onInventoryOpen(event: ContainerInitializedEvent) {
        inWardrobe = inventoryNameRegex.matches(event.title)

        if (inWardrobe) processInventory(event.title, event.itemStacks)
    }

    @Subscription
    fun onInventoryClose(event: ContainerCloseEvent) {
        inWardrobe = false
        currentPage = 0
    }

    @Subscription
    fun onProfileSwitch(event: ProfileChangeEvent) {
        val slotCount = max(
            slots.size.roundToNextMultipleOf(WARDROBE_SLOTS_PER_PAGE),
            WARDROBE_SLOTS_PER_PAGE * 3,
        )
        repeat(slotCount) { index ->
            val incr = index + 1
            val foundSlot = slots.any { it.id == incr }
            if (!foundSlot) {
                val emptySlot = WardrobeSlot(incr, emptyArmor, true)
                LoadoutStorage.updateArmorSlot(emptySlot)
            }
        }
    }

    private fun ItemStack.takeOrEmpty() = takeIf { it !in ItemTag.GLASS_PANES } ?: ItemStack.EMPTY

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi wardrobe armor") {
            then("copy") {
                callback {
                    val currentSlot = "Current Slot: $currentSlot"
                    val slots =
                        slots.map { "Id: ${it.id} - Armor: ${it.slots.map { a -> a.hoverName.stripped }} - Locked: ${it.locked}" }

                    Text.of("[SkyBlockAPI] Copied Armor Wardrobe Data to clipboard.") {
                        this.color = TextColor.YELLOW
                    }.send()

                    McClient.clipboard = "$currentSlot\n${slots.joinToString("\n")}"
                }
            }
            then("reset") {
                callback {
                    Text.of("[SkyBlockAPI] Reset Armor Wardrobe Data.") {
                        this.color = TextColor.YELLOW
                    }.send()
                    LoadoutStorage.clearArmor()
                }
            }
        }
    }
}
