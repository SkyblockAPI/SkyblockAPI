package tech.thatgravyboat.skyblockapi

import me.owdding.ktmodules.Module
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.item.replaceVisually
import tech.thatgravyboat.skyblockapi.utils.extentions.getRawLore
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch

@Module
object Pest {
    val regex = "ൠ This plot has (?<amount>.*) Pests?!".toRegex()

    @Subscription
    @InventoryTitle("Configure Plots")
    fun onInv(event: InventoryChangeEvent) {
        regex.anyMatch(event.item.getRawLore(), "amount") { (amount) ->
            val amount = amount.toIntValue().takeUnless { it == 0 } ?: return@anyMatch
            event.item.replaceVisually {
                copyFrom(event.item)
                backgroundItem = Items.LIME_STAINED_GLASS_PANE.defaultInstance
                customSlotText = "$amount"
            }
        }
    }
}
