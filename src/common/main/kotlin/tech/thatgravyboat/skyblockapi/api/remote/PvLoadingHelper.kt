package tech.thatgravyboat.skyblockapi.api.remote

import me.owdding.ktmodules.Module
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription.Companion.LOWEST
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvOpenedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvRequired
import tech.thatgravyboat.skyblockapi.utils.builders.TooltipBuilder
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.hover

@Module
object PvLoadingHelper {

    private val list: MutableSet<LoadedData> = mutableSetOf()

    fun markLoaded(data: LoadedData) = list.add(data)

    @Subscription(priority = LOWEST)
    @OptIn(SkyBlockPvRequired::class)
    private fun SkyBlockPvOpenedEvent.postPvLoad() {
        if (list.isEmpty()) return
        Text.debug("Loaded some data from pv! ") {
            append("(hover)") {
                this.color = TextColor.GRAY
            }
            this.hover = TooltipBuilder.multiline {
                add("The following data was loaded:") {
                    this.color = TextColor.GRAY
                }
                list.forEach { data ->
                    add("- ") {
                        append(data.component)
                        this.color = TextColor.GRAY
                    }
                }
            }
        }.send()
        list.clear()
    }
}

enum class LoadedData(val component: MutableComponent) {
    TROPHY_FISH(Text.of("Trophy Fish Data")),
    SLAYER(Text.of("Slayer Progress")),
    ENDERCHEST(Text.of("Storage (Ender Chest)")),
    BACKPACK(Text.of("Storage (Backpack)")),
    ;
}
