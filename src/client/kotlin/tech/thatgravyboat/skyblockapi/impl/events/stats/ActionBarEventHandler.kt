package tech.thatgravyboat.skyblockapi.impl.events.stats

import net.minecraft.util.StringUtil
import org.intellij.lang.annotations.Language
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.item.ArmorStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.*
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.Destructured
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.find
import tech.thatgravyboat.skyblockapi.utils.text.Text

data class ActionBarWidgetType(
    val widget: ActionBarWidget,
    val regex: Regex,
    val factory: (String, Destructured) -> ActionBarWidgetChangeEvent,
    val removalFactory: (Destructured) -> ActionBarWidgetChangeEvent,
) {

    constructor(
        widget: ActionBarWidget,
        @Language("RegExp") regex: String,
        removalFactory: (Destructured) -> ActionBarWidgetChangeEvent = { ActionBarWidgetChangeEvent(widget, it.string, "") },
        factory: (String, Destructured) -> ActionBarWidgetChangeEvent = { old, new -> ActionBarWidgetChangeEvent(widget, old, new.string) },
    ) : this(
        widget,
        RegexGroup.ACTIONBAR_WIDGET.create(widget.name.lowercase(), regex),
        factory,
        removalFactory,
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
        // §b319/319✎
        ActionBarWidgetType(ActionBarWidget.MANA, "§.(?<mana>[\\d,]+)/(?<maxmana>[\\d,]+)✎ (?:Mana)?") { old, it ->
            ManaActionBarWidgetChangeEvent(it["mana"].toIntValue(), it["maxmana"].toIntValue(), old, it.string)
        },
        // §3400ʬ
        ActionBarWidgetType(
            ActionBarWidget.OVERFLOW_MANA, "§.(?<overflow>[\\d,]+)ʬ",
            {
                OverflowManaActionBarWidgetChangeEvent(0, it.string, "")
            },
        ) { old, it ->
            OverflowManaActionBarWidgetChangeEvent(it["overflow"].toIntValue(), old, it.string)
        },
        // §c§lNOT ENOUGH MANA
        ActionBarWidgetType(ActionBarWidget.NO_MANA, "§c§lNOT ENOUGH MANA"),
        // §a§lⓩⓩⓩ§2§lⓄⓄ
        // §a§lⓩⓩⓩⓩⓩ§2§l
        ActionBarWidgetType(ActionBarWidget.CHARGES, "§a§l(?<maxcharges>(?<charges>ⓩ*)§2§lⓄ*)"),
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
        // §6§l10ᝐ
        // §65ᝐ
        ActionBarWidgetType(
            ActionBarWidget.ARMOR_STACK, "§6(?:§l)?(?<amount>\\d+)(?<type>[ᝐ⁑|҉Ѫ⚶])",
            {
                ArmorStackActionBarWidgetChangeEvent(0, ArmorStack.fromString(it["type"]), it.string, "")
            },
        ) { old, it ->
            ArmorStackActionBarWidgetChangeEvent(it["amount"].toIntValue(), ArmorStack.fromString(it["type"]), old, it.string)
        },
        // §a |||
        ActionBarWidgetType(ActionBarWidget.CELLS_ALIGNMENT, "§a \\|{3}"),
        // §71/6 Secrets
        ActionBarWidgetType(
            ActionBarWidget.SECRETS, "§.(?<current>[\\d,]+)/(?<max>[\\d,]+) Secrets",
            {
                SecretsActionBarWidgetChangeEvent(0, 0, it.string, "")
            },
        ) { old, it ->
            SecretsActionBarWidgetChangeEvent(it["current"].toIntValue(), it["max"].toIntValue(), old, it.string)
        },
        // §2936/3k Drill Fuel
        ActionBarWidgetType(ActionBarWidget.DRILL_FUEL, "§2(?<current>\\d+)/(?<max>\\d+[kmb]?) Drill Fuel") { old, it ->
            DrillActionBarWidgetChangeEvent(it["current"].parseFormattedInt(), it["max"].parseFormattedInt(), old, it.string)
        },
    )

    private val widgets = mutableMapOf<ActionBarWidget, String>()
    private val widgetsToHide = mutableListOf<String>()

    @Subscription
    fun onActionbarReceivedPre(event: ActionBarReceivedEvent.Pre) {
        widgetsToHide.clear()

        val parts = event.coloredText.split("     ")
        val output = parts.toMutableList()
        val foundWidgets = mutableSetOf<ActionBarWidget>()
        for (p in parts) {
            var part = p
            for (type in types) {
                type.regex.find(part) {
                    if (RenderActionBarWidgetEvent(type.widget).post(SkyBlockAPI.eventBus)) {
                        widgetsToHide.add(it.string)
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
        }
        for (widget in widgets.keys - foundWidgets) {
            val old = widgets[widget] ?: ""
            val type = types.find { it.widget == widget }
            val found = type?.regex?.find(old) {
                type.removalFactory(it).post()
            } ?: false
            if (!found) ActionBarWidgetChangeEvent(widget, old, "").post()
            widgets.remove(widget)
        }

        if (output.isEmpty()) {
            event.cancel()
        }
    }

    @Subscription
    fun onActionbarReceivedPost(event: ActionBarReceivedEvent.Post) {
        event.component = Text.of(
            event.coloredText.split("     ")
                .map {
                    var output = it
                    widgetsToHide.removeIf { widget ->
                        val before = output
                        output = output.replace(widget, "")
                        before != output
                    }
                    output
                }
                .filter { !StringUtil.stripColor(it).isBlank() }
                .joinToString("     ") { it.trimIgnoreColor() },
        )
    }
}
