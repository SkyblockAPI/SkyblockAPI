package tech.thatgravyboat.skyblockapi.api.profile.quiver

import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.stored.QuiverStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.screen.PlayerHotbarChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module

@Module
object QuiverAPI {

    var currentArrow: String? = null
        private set

    var currentAmount: Int?
        get() = arrows[currentArrow]
        private set(value) {
            QuiverStorage.updateArrow(currentArrow ?: return, value ?: return)
        }

    val arrows: Map<String, Int>
        get() = QuiverStorage.arrows

    @Subscription
    fun onHotbarChange(event: PlayerHotbarChangeEvent) {
        if (event.slot != 8) return
        if (event.item.item != Items.ARROW) return
    }

}
