package tech.thatgravyboat.skyblockapi.impl.events.stats

import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidget
import tech.thatgravyboat.skyblockapi.api.events.info.ActionBarWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.find
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

@Module
object ActionBarEventHandler {

    private val regexes = mapOf(
        // §c1,303/1,303❤
        ActionBarWidget.HEALTH to Regexes.create(
            "actionbar.health",
            "§.(?<health>[\\d,]+)/(?<maxhealth>[\\d,]+)❤"
        ),
        // §a245§a❈ Defense
        ActionBarWidget.DEFENSE to Regexes.create(
            "actionbar.defense",
            "§.(?<defense>[\\d,]+)❈ Defense"
        ),
        // §b319/319✎ Mana
        ActionBarWidget.MANA to Regexes.create(
            "actionbar.mana",
            "§.(?<mana>[\\d,]+)/(?<maxmana>[\\d,]+)✎ Mana"
        ),
        // §b+3 SkyBlock XP §7(Accessory Bag§7)§b (68/100)
        ActionBarWidget.SKYBLOCK_XP to Regexes.create(
            "actionbar.skyblock_xp",
            "§.\\+(?<amount>[\\d,]+) SkyBlock XP"
        ),
        // §b-100 Mana (§6Dragon Rage§b)
        ActionBarWidget.ABILITY to Regexes.create(
            "actionbar.ability",
            "§.-?(?<amount>[\\d,]+) Mana \\(§.(?<ability>[^)]+)§.\\)"
        ),
        // §3+1.7 Mining (38.19%)
        ActionBarWidget.SKILL_XP to Regexes.create(
            "actionbar.skill_xp",
            "§.\\+(?<amount>[\\d.]+) (?<skill>\\w+) \\((?<percent>[\\d.]+)%\\)"
        ),
        // §7⏣ §bLava Springs
        ActionBarWidget.LOCATION to Regexes.create(
            "actionbar.location",
            "§7⏣ §.(?<location>.+)"
        )
    )

    private val widgets = mutableMapOf<ActionBarWidget, String>()

    @Subscription
    fun onActionbarReceived(event: ActionBarReceivedEvent) {
        val parts = event.coloredText.split("     ")
        val foundWidgets = mutableSetOf<ActionBarWidget>()
        for ((widget, regex) in regexes) {
            for (part in parts) {
                regex.find(part) {
                    val old = widgets[widget] ?: ""
                    val new = it[0]
                    foundWidgets.add(widget)
                    if (old != new) {
                        widgets[widget] = new
                        ActionBarWidgetChangeEvent(widget, old, new).post(SkyBlockAPI.eventBus)
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
