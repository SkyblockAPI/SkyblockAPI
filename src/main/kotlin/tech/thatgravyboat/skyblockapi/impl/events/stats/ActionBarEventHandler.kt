package tech.thatgravyboat.skyblockapi.impl.events.stats

import me.owdding.ktmodules.Module
import net.minecraft.network.chat.Component
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.item.ArmorStack
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ActionBarReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.*
import tech.thatgravyboat.skyblockapi.api.remote.hypixel.HypixelSkillAPI
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.component.ComponentRegex
import tech.thatgravyboat.skyblockapi.utils.regex.component.Destructured
import tech.thatgravyboat.skyblockapi.utils.regex.component.find
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findOrNull
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.remove
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.split
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.toStringWithFormattingCodes
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.trim

data class ActionBarWidgetType(
    val widget: ActionBarWidget,
    val regex: ComponentRegex,
    val factory: (Component, Component, Destructured) -> ActionBarWidgetUpdateEvent?,
    val legacyFactory: (String, String) -> ActionBarWidgetChangeEvent? = { old, new ->
        ActionBarWidgetChangeEvent(widget, old, new)
    },
)

@Module
object ActionBarEventHandler {

    private val types = listOf(
        // Health: §c1,303/1,303❤
        ActionBarWidgetType(
            ActionBarWidget.HEALTH,
            ComponentRegex("(?<health>[\\d,]+)/(?<maxhealth>[\\d,]+)❤(?:\\+§.[\\d,]+.)?"),
            factory = { old, new, it ->
                ActionBarWidgetUpdateEvent.Health(it["health"].toIntValue(), it["maxhealth"].toIntValue(), old, new)
            },
            legacyFactory = { old, new ->
                Regex("§.(?<health>[\\d,]+)/(?<maxhealth>[\\d,]+)❤(?:\\+§.[\\d,]+.)?").findOrNull(new) {
                    HealthActionBarWidgetChangeEvent(it["health"].toIntValue(), it["maxhealth"].toIntValue(), old, new)
                }
            },
        ),
        // Defense: §a245§a❈ Defense
        ActionBarWidgetType(
            ActionBarWidget.DEFENSE,
            ComponentRegex("(?<defense>[\\d,]+)❈ Defense"),
            factory = { old, new, it ->
                ActionBarWidgetUpdateEvent.Defense(it["defense"].toIntValue(), old, new)
            },
            legacyFactory = { old, new ->
                Regex("§.(?<defense>[\\d,]+)§.❈ Defense").findOrNull(new) {
                    DefenseActionBarWidgetChangeEvent(it["defense"].toIntValue(), old, new)
                }
            },
        ),
        // Mana: §b319/319✎ Mana
        ActionBarWidgetType(
            ActionBarWidget.MANA,
            ComponentRegex("(?<mana>[\\d,]+)/(?<maxmana>[\\d,]+)✎ Mana"),
            factory = { old, new, it ->
                ActionBarWidgetUpdateEvent.Mana(it["mana"].toIntValue(), it["maxmana"].toIntValue(), old, new)
            },
            legacyFactory = { old, new ->
                Regex("§.(?<mana>[\\d,]+)/(?<maxmana>[\\d,]+)✎ Mana").findOrNull(new) {
                    ManaActionBarWidgetChangeEvent(it["mana"].toIntValue(), it["maxmana"].toIntValue(), old, new)
                }
            },
        ),
        // Overflow Mana: §3400ʬ
        ActionBarWidgetType(
            ActionBarWidget.OVERFLOW_MANA,
            ComponentRegex("(?<overflow>[\\d,]+)ʬ"),
            factory = { old, new, it ->
                ActionBarWidgetUpdateEvent.OverflowMana(it["overflow"].toIntValue(), old, new)
            },
            legacyFactory = { old, new ->
                Regex("§.(?<overflow>[\\d,]+)ʬ").findOrNull(new) {
                    OverflowManaActionBarWidgetChangeEvent(it["overflow"].toIntValue(), old, new)
                }
            },
        ),
        // Skill XP (Percent)
        ActionBarWidgetType(
            ActionBarWidget.SKILL_XP,
            ComponentRegex("\\+(?<amount>[\\d.]+) (?<skill>\\w+) \\((?<percent>[\\d.]+)%\\)"),
            factory = { old, new, it ->
                val skill = HypixelSkillAPI.Skill.getByName(it["skill"].toString()) ?: return@ActionBarWidgetType null
                ActionBarWidgetUpdateEvent.SkillXpPercent(it["amount"].toFloatValue(), skill, it["percent"].toFloatValue() / 100f, old, new)
            },
            legacyFactory = { old, new ->
                Regex("§.\\+(?<amount>[\\d.]+) (?<skill>\\w+) \\((?<percent>[\\d.]+)%\\)").findOrNull(new) {
                    val skill = HypixelSkillAPI.Skill.getByName(it["skill"] ?: "") ?: return@findOrNull null
                    SkillXpPercentActionBarWidgetChangeEvent(it["amount"].toFloatValue(), skill, it["percent"].toFloatValue() / 100f, old, new)
                }
            },
        ),
        // Skill XP (Literal)
        ActionBarWidgetType(
            ActionBarWidget.SKILL_XP_LITERAL,
            ComponentRegex("\\+(?<amount>[\\d.,]+) (?<skill>\\w+) \\((?<current>[\\d,]+)/(?<needed>[\\d,]+[kmb]?)\\)"),
            factory = { old, new, it ->
                val skill = HypixelSkillAPI.Skill.getByName(it["skill"].toString()) ?: return@ActionBarWidgetType null
                ActionBarWidgetUpdateEvent.SkillXpLiteral(
                    it["amount"].toFloatValue(),
                    skill,
                    it["current"].parseFormattedLong(),
                    it["needed"].parseFormattedLong(),
                    old,
                    new,
                )
            },
            legacyFactory = { old, new ->
                Regex("§.\\+(?<amount>[\\d.,]+) (?<skill>\\w+) \\((?<current>[\\d,]+)/(?<needed>[\\d,]+[kmb]?)\\)").findOrNull(new) {
                    val skill = HypixelSkillAPI.Skill.getByName(it["skill"] ?: "") ?: return@findOrNull null
                    SkillXpLiteralActionBarWidgetChangeEvent(
                        it["amount"].toFloatValue(),
                        skill,
                        it["current"].parseFormattedLong(),
                        it["needed"].parseFormattedLong(),
                        old,
                        new,
                    )
                }
            },
        ),
        // Rift Time
        ActionBarWidgetType(
            ActionBarWidget.RIFT_TIME,
            ComponentRegex("(?<time>.+)ф Left"),
            factory = { old, new, it ->
                val duration = it["time"]?.stripped?.parseDuration() ?: return@ActionBarWidgetType null
                ActionBarWidgetUpdateEvent.RiftTime(duration, old, new)
            },
            legacyFactory = { old, new ->
                Regex("§.(?<time>.+)ф Left").findOrNull(new) {
                    val duration = it["time"].parseDuration() ?: return@findOrNull null
                    RiftTimeActionBarWidgetChangeEvent(duration, old, new)
                }
            },
        ),
        // Armadillo
        ActionBarWidgetType(
            ActionBarWidget.ARMADILLO,
            ComponentRegex("Armadillo Energy: (?:.| )+ (?<current>[\\d.]+)§./(?<max>[\\d.]+)"),
            factory = { old, new, it ->
                ActionBarWidgetUpdateEvent.Armadillo(it["current"].toFloatValue(), it["max"].toFloatValue(), old, new)
            },
            legacyFactory = { old, new ->
                Regex("§.Armadillo Energy: (§.| )+ §.(?<current>[\\d.]+)§./§.(?<max>[\\d.]+)").findOrNull(new) {
                    ArmadilloActionBarWidgetChangeEvent(it["current"].toFloatValue(), it["max"].toFloatValue(), old, new)
                }
            },
        ),
        // Armor Stack
        ActionBarWidgetType(
            ActionBarWidget.ARMOR_STACK,
            ComponentRegex("(?<amount>\\d+)(?<type>[ᝐ⁑|҉Ѫ⚶])"),
            factory = { old, new, it ->
                val type = ArmorStack.fromString(it["type"].toString()) ?: return@ActionBarWidgetType null
                ActionBarWidgetUpdateEvent.ArmorStack(it["amount"].toIntValue(), type, old, new)
            },
            legacyFactory = { old, new ->
                Regex("§6(?:§l)?(?<amount>\\d+)(?<type>[ᝐ⁑|҉Ѫ⚶])").findOrNull(new) {
                    val type = ArmorStack.fromString(it["type"]) ?: return@findOrNull null
                    ArmorStackActionBarWidgetChangeEvent(it["amount"].toIntValue(), type, old, new)
                }
            },
        ),
        // Secrets
        ActionBarWidgetType(
            ActionBarWidget.SECRETS,
            ComponentRegex("(?<current>[\\d,]+)/(?<max>[\\d,]+) Secrets"),
            factory = { old, new, it ->
                ActionBarWidgetUpdateEvent.Secrets(it["current"].toIntValue(), it["max"].toIntValue(), old, new)
            },
            legacyFactory = { old, new ->
                Regex("§.(?<current>[\\d,]+)/(?<max>[\\d,]+) Secrets").findOrNull(new) {
                    SecretsActionBarWidgetChangeEvent(it["current"].toIntValue(), it["max"].toIntValue(), old, new)
                }
            },
        ),
        // Drill Fuel
        ActionBarWidgetType(
            ActionBarWidget.DRILL_FUEL,
            ComponentRegex("(?<current>\\d+)/(?<max>\\d+[kmb]?) Drill Fuel"),
            factory = { old, new, it ->
                ActionBarWidgetUpdateEvent.Drill(it["current"].parseFormattedInt(), it["max"].parseFormattedInt(), old, new)
            },
            legacyFactory = { old, new ->
                Regex("§2(?<current>\\d+)/(?<max>\\d+[kmb]?) Drill Fuel").findOrNull(new) {
                    DrillActionBarWidgetChangeEvent(it["current"].parseFormattedInt(), it["max"].parseFormattedInt(), old, new)
                }
            },
        ),
        // Pressure
        ActionBarWidgetType(
            ActionBarWidget.PRESSURE,
            ComponentRegex("Pressure: ❍(?<pressure>\\d+)%"),
            factory = { old, new, it ->
                ActionBarWidgetUpdateEvent.Pressure(it["pressure"].toIntValue(), old, new)
            },
            legacyFactory = { old, new ->
                Regex("§9Pressure: ❍(?<pressure>\\d+)%").findOrNull(new) {
                    PressureActionBarWidgetChangeEvent(it["pressure"].toIntValue(), old, new)
                }
            },
        ),
        // Location
        ActionBarWidgetType(
            ActionBarWidget.LOCATION,
            ComponentRegex("⏣ (?<location>.+)"),
            factory = { old, new, it ->
                val location = it["location"] ?: return@ActionBarWidgetType null
                ActionBarWidgetUpdateEvent.Location(location, old, new)
            },
        ),
        // SkyBlock XP
        ActionBarWidgetType(
            ActionBarWidget.SKYBLOCK_XP,
            ComponentRegex("\\+(?<amount>[\\d,]+) SkyBlock XP"),
            factory = { old, new, it ->
                ActionBarWidgetUpdateEvent.SkyBlockXp(it["amount"].parseFormattedLong(), old, new)
            },
        ),
        // Ability
        ActionBarWidgetType(
            ActionBarWidget.ABILITY,
            ComponentRegex("-?(?<amount>[\\d,]+) Mana \\((?<ability>[^)]+)\\)"),
            factory = { old, new, it ->
                val ability = it["ability"] ?: return@ActionBarWidgetType null
                ActionBarWidgetUpdateEvent.Ability(it["amount"].toIntValue(), ability, old, new)
            },
        ),
        // Charges
        ActionBarWidgetType(
            ActionBarWidget.CHARGES,
            ComponentRegex("(?<maxcharges>(?<charges>ⓩ*)§2§lⓄ*)"),
            factory = { old, new, it ->
                val maxCharges = it["maxcharges"] ?: return@ActionBarWidgetType null
                ActionBarWidgetUpdateEvent.Charges(it["charges"].toString().length, maxCharges, old, new)
            },
        ),
        // Cells Alignment
        ActionBarWidgetType(
            ActionBarWidget.CELLS_ALIGNMENT,
            ComponentRegex("(?<alignment>\\|{1,3})"),
            factory = { old, new, it ->
                ActionBarWidgetUpdateEvent.CellsAlignment(it["alignment"].toString().length, old, new)
            },
        ),
        // Not Enough Mana
        ActionBarWidgetType(
            ActionBarWidget.NO_MANA,
            ComponentRegex("NOT ENOUGH MANA"),
            factory = { old, new, _ -> ActionBarWidgetUpdateEvent.NoMana(old, new) },
        ),
    )

    private val widgets = mutableMapOf<ActionBarWidget, Component>()
    private val widgetsToHide = mutableListOf<Component>()

    @Subscription
    @OnlyOnSkyBlock
    fun onActionbarReceivedPre(event: ActionBarReceivedEvent.Pre) {
        widgetsToHide.clear()

        val parts = event.component.split("     ")
        val foundWidgets = mutableSetOf<ActionBarWidget>()

        for (part in parts) {
            for (type in types) {
                type.regex.find(part) { match ->
                    if (RenderActionBarWidgetEvent(type.widget).post(SkyBlockAPI.eventBus)) {
                        widgetsToHide.add(match.component)
                    }

                    val oldComp = widgets[type.widget] ?: Component.empty()
                    val newComp = match.component
                    foundWidgets.add(type.widget)

                    if (oldComp != newComp) {
                        widgets[type.widget] = newComp
                        type.factory(oldComp, newComp, match)?.post()
                        type.legacyFactory(oldComp.toStringWithFormattingCodes(), newComp.toStringWithFormattingCodes())?.post()
                    }
                }
            }
        }

        val missing = widgets.keys - foundWidgets
        for (widget in missing) {
            val oldComp = widgets[widget] ?: Component.empty()
            val type = types.find { it.widget == widget }
            ActionBarWidgetUpdateEvent.Unknown(widget, oldComp, Component.empty()).post()
            type?.legacyFactory?.invoke(oldComp.toStringWithFormattingCodes(), "")?.post()
            widgets.remove(widget)
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    fun onActionbarReceivedPost(event: ActionBarReceivedEvent.Post) {
        var currentBar = event.component

        val iterator = widgetsToHide.iterator()
        while (iterator.hasNext()) {
            val toHide = iterator.next()
            val nextBar = currentBar.remove(toHide)

            if (nextBar != currentBar) {
                currentBar = nextBar
                iterator.remove()
            }
        }

        event.component = currentBar.trim()
    }
}
