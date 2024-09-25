package tech.thatgravyboat.skyblockapi.impl.events.stats

import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.*
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.Destructured
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.find
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

data class ActionBarWidgetType(
    val widget: ActionBarWidget,
    val regex: Regex,
    val factory: (String, Destructured) -> ActionBarWidgetChangeEvent
) {

    constructor(
        widget: ActionBarWidget,
        @Language("RegExp") regex: String,
        factory: (String, Destructured) -> ActionBarWidgetChangeEvent = { old, new -> ActionBarWidgetChangeEvent(widget, old, new.string) }
    ) : this(
        widget,
        Regexes.create("actionbar.${widget.name.lowercase()}", regex),
        factory
    )
}

@Module
object ActionBarEventHandler {

    private val types = listOf(
        // §c1,303/1,303❤
        ActionBarWidgetType(ActionBarWidget.HEALTH, "§.(?<health>[\\d,]+)/(?<maxhealth>[\\d,]+)❤(?:\\+§.[\\d,]+.)?") { old, it ->
            HealthActionBarWidgetChangeEvent(it["health"].toIntValue(), it["maxhealth"].toIntValue(), old, it.string)
        },
        // §a245§a❈ Defense
        ActionBarWidgetType(ActionBarWidget.DEFENSE, "§.(?<defense>[\\d,]+)❈ Defense") { old, it ->
            DefenseActionBarWidgetChangeEvent(it["defense"].toIntValue(), old, it.string)
        },
        // §b319/319✎ Mana
        ActionBarWidgetType(ActionBarWidget.MANA, "§.(?<mana>[\\d,]+)/(?<maxmana>[\\d,]+)✎ Mana") { old, it ->
            ManaActionBarWidgetChangeEvent(it["mana"].toIntValue(), it["maxmana"].toIntValue(), old, it.string)
        },
        // §b+3 SkyBlock XP §7(Accessory Bag§7)§b (68/100)
        ActionBarWidgetType(ActionBarWidget.SKYBLOCK_XP, "§.\\+(?<amount>[\\d,]+) SkyBlock XP"),
        // §b-100 Mana (§6Dragon Rage§b)
        ActionBarWidgetType(ActionBarWidget.ABILITY, "§.-?(?<amount>[\\d,]+) Mana \\(§.(?<ability>[^)]+)§.\\)"),
        // §3+1.7 Mining (38.19%)
        ActionBarWidgetType(ActionBarWidget.SKILL_XP, "§.\\+(?<amount>[\\d.]+) (?<skill>\\w+) \\((?<percent>[\\d.]+)%\\)"),
        // §7⏣ §bLava Springs
        ActionBarWidgetType(ActionBarWidget.LOCATION, "§7⏣ §.(?<location>.+)")
    )

    private val widgets = mutableMapOf<ActionBarWidget, String>()

    @Subscription
    fun onActionbarReceived(event: ActionBarReceivedEvent) {
        val parts = event.coloredText.split("     ")
        val foundWidgets = mutableSetOf<ActionBarWidget>()
        for (type in types) {
            for (part in parts) {
                type.regex.find(part) {
                    val old = widgets[type.widget] ?: ""
                    val new = it.string
                    foundWidgets.add(type.widget)
                    if (old != new) {
                        widgets[type.widget] = new
                        type.factory(old, it).post(SkyBlockAPI.eventBus)
                    }
                }
            }
        }
        for (widget in widgets.keys - foundWidgets) {
            val old = widgets[widget] ?: ""
            ActionBarWidgetChangeEvent(widget, old, "").post(SkyBlockAPI.eventBus)
            widgets.remove(widget)
        }
    }
}
