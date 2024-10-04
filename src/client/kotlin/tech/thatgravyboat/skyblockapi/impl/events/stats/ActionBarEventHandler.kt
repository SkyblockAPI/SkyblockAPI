package tech.thatgravyboat.skyblockapi.impl.events.stats

import net.minecraft.util.StringUtil
import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.*
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseDuration
import tech.thatgravyboat.skyblockapi.utils.extentions.toFloatValue
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.Destructured
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.find
import tech.thatgravyboat.skyblockapi.utils.regex.Regexes

data class ActionBarWidgetType(
    val widget: ActionBarWidget,
    val regex: Regex,
    val factory: (String, Destructured) -> ActionBarWidgetChangeEvent,
) {

    constructor(
        widget: ActionBarWidget,
        @Language("RegExp") regex: String,
        factory: (String, Destructured) -> ActionBarWidgetChangeEvent = { old, new -> ActionBarWidgetChangeEvent(widget, old, new.string) },
    ) : this(
        widget,
        Regexes.create("actionbar.${widget.name.lowercase()}", regex),
        factory,
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
        ActionBarWidgetType(ActionBarWidget.DEFENSE, "§.(?<defense>[\\d,]+)§.❈ Defense") { old, it ->
            DefenseActionBarWidgetChangeEvent(it["defense"].toIntValue(), old, it.string)
        },
        // §b319/319✎ Mana
        ActionBarWidgetType(ActionBarWidget.MANA, "§.(?<mana>[\\d,]+)/(?<maxmana>[\\d,]+)✎ Mana") { old, it ->
            ManaActionBarWidgetChangeEvent(it["mana"].toIntValue(), it["maxmana"].toIntValue(), old, it.string)
        },
        // §a§lⓩⓩⓩ§2§lⓄⓄ
        // §a§lⓩⓩⓩⓩⓩ§2§l
        ActionBarWidgetType(ActionBarWidget.CHARGES, "§a§l(?<maxcharges>(?<charges>ⓩ*)§2§l)"),
        // §b+3 SkyBlock XP §7(Accessory Bag§7)§b (68/100)
        ActionBarWidgetType(ActionBarWidget.SKYBLOCK_XP, "§.\\+(?<amount>[\\d,]+) SkyBlock XP"),
        // §b-100 Mana (§6Dragon Rage§b)
        ActionBarWidgetType(ActionBarWidget.ABILITY, "§.-?(?<amount>[\\d,]+) Mana \\(§.(?<ability>[^)]+)§.\\)"),
        // §3+1.7 Mining (38.19%)
        ActionBarWidgetType(ActionBarWidget.SKILL_XP, "§.\\+(?<amount>[\\d.]+) (?<skill>\\w+) \\((?<percent>[\\d.]+)%\\)"),
        // §7⏣ §bLava Springs
        ActionBarWidgetType(ActionBarWidget.LOCATION, "§.⏣ §.(?<location>.+)"),
        // §750m40sф Left
        ActionBarWidgetType(ActionBarWidget.RIFT_TIME, "§.(?<time>.+)ф Left") { old, it ->
            RiftTimeActionBarWidgetChangeEvent(it["time"].parseDuration(), old, it.string)
        },
        // §6Armadillo Energy: §e§l§m               §r §6248.5§e/§6250
        ActionBarWidgetType(ActionBarWidget.ARMADILLO, "§.Armadillo Energy: (§.| )+ §.(?<current>[\\d.]+)§./§.(?<max>[\\d.]+)") { old, it ->
            ArmadilloActionBarWidgetChangeEvent(it["current"].toFloatValue(), it["max"].toFloatValue(), old, it.string)
        },
    )

    private val widgets = mutableMapOf<ActionBarWidget, String>()

    @Subscription
    fun onActionbarReceived(event: ActionBarReceivedEvent) {
        val parts = event.coloredText.split("     ")
        val output = parts.toMutableList()
        val foundWidgets = mutableSetOf<ActionBarWidget>()
        for (p in parts) {
            var part = p
            for (type in types) {
                type.regex.find(part) {
                    if (RenderActionBarWidgetEvent(type.widget).post(SkyBlockAPI.eventBus)) {
                        part = part.replace(it.string, "")
                    }
                    val old = widgets[type.widget] ?: ""
                    val new = it.string
                    foundWidgets.add(type.widget)
                    if (old != new) {
                        widgets[type.widget] = new
                        type.factory(old, it).post(SkyBlockAPI.eventBus)
                    }
                }
            }
            if (StringUtil.stripColor(part).isBlank()) {
                output.remove(part)
            } else {
                output[output.indexOf(part)] = part
            }
        }
        for (widget in widgets.keys - foundWidgets) {
            val old = widgets[widget] ?: ""
            ActionBarWidgetChangeEvent(widget, old, "").post(SkyBlockAPI.eventBus)
            widgets.remove(widget)
        }

        if (output.isEmpty()) {
            event.cancel()
        } else if (output != parts) {
            event.coloredText = output.joinToString("     ")
        }
    }
}
