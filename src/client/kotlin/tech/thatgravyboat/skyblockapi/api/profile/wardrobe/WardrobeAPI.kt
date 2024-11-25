package tech.thatgravyboat.skyblockapi.api.profile.wardrobe

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.WardrobeStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerCloseEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTagKey
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object WardrobeAPI {
    private val wardrobeGroup = RegexGroup.INVENTORY.group("wardrobe")

    private val inventoryNameRegex = wardrobeGroup.create(
        "title",
        "Wardrobe \\((?<currentPage>\\d+)/\\d+\\)",
    )

    private val equippedRegex = wardrobeGroup.create(
        "equip",
        "Slot \\d+: Equipped",
    )


    var inWardrobe = false
        private set

    val slots get() = WardrobeStorage.slots.toList()

    val currentSlot: Int? get() = WardrobeStorage.currentSlot


    private fun processInventory(title: String, items: List<ItemStack>) {
        var currentPage = 0
        inventoryNameRegex.match(title, "currentPage") { (cp) ->
            cp.toIntOrNull()?.let { currentPage = it }
        }

        for (index in 0..8) {
            val selectStack = items[index + 36]
            val id = 9 * currentPage + index - 8
            var locked = false

            if (selectStack.item == Items.RED_DYE) {
                locked = true
            } else if (equippedRegex.match(selectStack.hoverName.stripped)) {
                WardrobeStorage.updateCurrentSlot(id)
            }

            val helmetStack = items[index].takeOrEmpty()
            val chestplateStack = items[index + 9].takeOrEmpty()
            val leggingsStack = items[index + 18].takeOrEmpty()
            val bootsStack = items[index + 27].takeOrEmpty()

            val slot = WardrobeSlot(id, mutableListOf(helmetStack, chestplateStack, leggingsStack, bootsStack), locked)

            WardrobeStorage.updateSlot(slot)
        }
    }

    @Subscription
    fun onInventoryUpdate(event: ContainerChangeEvent) {
        inWardrobe = inventoryNameRegex.matches(event.title)

        if (inWardrobe) processInventory(event.title, event.inventory)
    }

    @Subscription
    fun onInventoryOpen(event: ContainerInitializedEvent) {
        inWardrobe = inventoryNameRegex.matches(event.title)

        if (inWardrobe) processInventory(event.title, event.itemStacks)
    }

    @Subscription
    fun onInventoryClose(event: ContainerCloseEvent) {
        inWardrobe = false
    }

    private fun ItemStack.takeOrEmpty() = takeIf { it !in ItemTagKey.GLASS_PANES } ?: ItemStack.EMPTY

    @Subscription
    fun onCommandsRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("wardrobe") {
                then("copy") {
                    callback {
                        val currentSlot = "Current Slot: $currentSlot"
                        val slots =
                            slots.map { "Id: ${it.id} - Armor: ${it.armor.map { a -> a.hoverName.stripped }} - Locked: ${it.locked}" }

                        Text.of("[SkyBlockAPI] Copied Wardrobe Data to clipboard.") {
                            this.color = TextColor.YELLOW
                        }.send()

                        McClient.clipboard = "$currentSlot\n${slots.joinToString("\n")}"
                    }
                }
                then("reset") {
                    callback {
                        Text.of("[SkyBlockAPI] Reset Wardrobe Data.") {
                            this.color = TextColor.YELLOW
                        }.send()
                        WardrobeStorage.clear()
                    }
                }
            }
        }
    }
}
