package tech.thatgravyboat.skyblockapi.api.profile.quiver

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockCategory
import tech.thatgravyboat.skyblockapi.api.data.stored.QuiverStorage
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.DebugBuilder
import tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerInventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.bank
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.bits
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.coopBank
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.copper
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.gems
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.kernels
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.motes
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.northStars
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.personalBank
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.purse
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.purseType
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.soulflow
import tech.thatgravyboat.skyblockapi.api.profile.currency.CurrencyAPI.sowdust
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockItemsRepo
import tech.thatgravyboat.skyblockapi.utils.ApiDebug
import tech.thatgravyboat.skyblockapi.utils.extentions.addOrPut
import tech.thatgravyboat.skyblockapi.utils.extentions.cleanName
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyFound
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.contains

@Module
object QuiverAPI {

    private val inventoryGroup = RegexGroup.INVENTORY.group("quiver")

    private val arrowCountRegex = inventoryGroup.create(
        "active",
        "Arrows Remaining: (?<amount>[\\d,.kmb]+)",
    )
    private val quiverInventoryRegex = inventoryGroup.create(
        "inventory",
        "^Quiver$"
    )

    var currentArrow: String?
        get() = QuiverStorage.currentArrow
        private set(value) {
            QuiverStorage.updateCurrent(value ?: return)
        }

    var currentAmount: Int?
        get() = arrows[currentArrow]
        private set(value) {
            QuiverStorage.updateArrow(currentArrow ?: return, value ?: return)
        }

    val arrows: Map<String, Int>
        get() = mutableArrows

    private inline val mutableArrows: MutableMap<String, Int>
        get() = QuiverStorage.arrows

    @Subscription
    fun onPlayerInventoryChange(event: PlayerInventoryChangeEvent) {
        val item = event.item
        if (item.getData(DataTypes.QUIVER_ARROW) != true) return
        arrowCountRegex.anyFound(item.getRawLore(), "amount") { (amount) ->
            currentArrow = SkyBlockItemsRepo.getIdByName(item.cleanName)
            if (currentArrow == null) return@anyFound
            currentAmount = amount.toIntValue()
        }
    }

    @Subscription
    fun onInventoryInitialized(event: ContainerInitializedEvent) {
        handleQuiverInventory(event.title, event.itemStacks)
    }

    @Subscription
    fun onInventoryChange(event: InventoryChangeEvent) {
        handleQuiverInventory(event.title, event.itemStacks)
    }

    private fun handleQuiverInventory(title: String, items: List<ItemStack>) {
        if (!quiverInventoryRegex.contains(title)) return

        val newArrows = buildMap {
            for (item in items) {
                val category = item.getData(DataTypes.CATEGORY)
                if (category != SkyBlockCategory.ARROW) continue
                val id = item.getData(DataTypes.ID) ?: continue
                addOrPut(id, item.count)
            }
        }
        QuiverStorage.setArrows(newArrows)
    }

    @ApiDebug("Quiver")
    internal fun debug(builder: DebugBuilder) = with(builder) {
        fields(
            ::currentArrow,
            ::currentAmount,
            ::arrows,
        )
    }

}
