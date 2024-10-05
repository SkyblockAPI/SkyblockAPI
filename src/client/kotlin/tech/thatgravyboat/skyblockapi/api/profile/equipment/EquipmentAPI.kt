package tech.thatgravyboat.skyblockapi.api.profile.equipment

import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.stored.EquipmentStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.level.RightClickEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryFullyLoadedEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.find
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped

@Module
object EquipmentAPI {

    private val inventoryNameRegex = RegexGroup.INVENTORY.group("equipment").create(
        "title",
        "Your Equipment and Stats"
    )

    private val chatEquipRegex = RegexGroup.CHAT.group("equipment").create(
        "equip",
        "You equipped a (?<item>.+)"
    )


    private var lastClickedEquipment: Pair<ItemStack, EquipmentSlot>? = null

    val equipment get(): Map<EquipmentSlot, ItemStack> = EquipmentStorage.equipment

    @Subscription
    fun onInventoryFullyLoad(event: InventoryFullyLoadedEvent) {
        if (!inventoryNameRegex.matches(event.title.stripped)) return
        EquipmentSlot.entries.forEach {
            EquipmentStorage.setEquipment(it, event.itemStacks[it.slot])
        }
    }

    @Subscription
    fun onInventoryChange(event: InventoryChangeEvent) {
        if (!inventoryNameRegex.matches(event.title.stripped)) return
        val slot = EquipmentSlot.entries.find { it.slot == event.slot } ?: return
        EquipmentStorage.setEquipment(slot, event.item)
    }

    @Subscription
    fun onRightClick(event: RightClickEvent) {
        val category = event.stack.getData(DataTypes.CATEGORY) ?: return
        val slot = EquipmentSlot.entries.find { category in it.categories } ?: return
        lastClickedEquipment = event.stack to slot
    }

    @Subscription
    fun onChat(event: ChatReceivedEvent) {
        val (item, slot) = lastClickedEquipment ?: return
        chatEquipRegex.find(event.text, "item") { (itemName) ->
            if (item.hoverName.stripped != itemName) return@find
            EquipmentStorage.setEquipment(slot, item)
            lastClickedEquipment = null
        }
    }

}
