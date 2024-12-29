package tech.thatgravyboat.skyblockapi.impl.debug

import com.mojang.brigadier.arguments.StringArgumentType
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidget
import tech.thatgravyboat.skyblockapi.api.events.info.RenderActionBarWidgetEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.profile.StatsAPI
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@Module
object DebugActionBar {

    private val widgetsToHide = mutableSetOf<ActionBarWidget>()

    @Subscription
    fun onCommandRegistration(event: RegisterCommandsEvent) {
        event.register("sbapi") {
            then("actionbar") {
                then("stats") {
                    callback {
                        McClient.clipboard = """
                            Health: ${StatsAPI.health}/${StatsAPI.maxHealth}
                            Mana: ${StatsAPI.mana}/${StatsAPI.maxMana} (${StatsAPI.overflowMana})
                            Defense: ${StatsAPI.defense}
                        """.trimIndent()

                        Text.of("[SkyBlockAPI] Stats Copied to Clipboard") {
                            this.color = TextColor.YELLOW
                        }.send()
                    }
                }

                then("hide") {
                    then("widget", StringArgumentType.greedyString(), ActionBarWidget.entries.map { it.name }) {

                        callback {
                            val widget = ActionBarWidget.valueOf(StringArgumentType.getString(this, "widget"))
                            if (widget in widgetsToHide) {
                                Text.of("[SkyBlockAPI] Unhiding widget $widget in action bar") {
                                    this.color = TextColor.YELLOW
                                }.send()
                                widgetsToHide.remove(widget)
                            } else {
                                Text.of("[SkyBlockAPI] Hiding widget $widget in action bar") {
                                    this.color = TextColor.YELLOW
                                }.send()
                                widgetsToHide.add(widget)
                            }
                        }
                    }
                }
            }
        }
    }

    @Subscription
    fun onWidgetShow(event: RenderActionBarWidgetEvent) {
        if (event.widget in widgetsToHide) {
            event.cancel()
        }
    }

}
