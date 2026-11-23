package tech.thatgravyboat.skyblockapi.api.area.mining.mineshaft

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.location.ServerDisconnectEvent
import tech.thatgravyboat.skyblockapi.api.events.location.mineshaft.CorpseEvent
import tech.thatgravyboat.skyblockapi.api.events.location.mineshaft.MineshaftEnteredEvent
import tech.thatgravyboat.skyblockapi.api.events.location.mineshaft.MineshaftFoundEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockEnchantmentsRepo
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyFound
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.findAll
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

//? if < 26.4
import tech.thatgravyboat.skyblockapi.api.events.location.mineshaft.CorpseSpawnEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.addOrPut
import tech.thatgravyboat.skyblockapi.utils.extentions.parseRomanOrArabic

@Module
public object MineshaftAPI {

    private val widgetGroup = RegexGroup.TABLIST_WIDGET.group("mineshaft")
    private val scoreboardGroup = RegexGroup.SCOREBOARD.group("mineshaft")
    private val chatGroup = RegexGroup.CHAT.group("mineshaft")

    private val scrapRegex = widgetGroup.create(
        "scrap",
        "^\\s*Scrap: (?<scrap>\\d+/\\d)$",
    )
    private val corpseRegex = widgetGroup.create(
        "corpse",
        "^\\s*(?<corpse>\\w+): (?<state>.+)$",
    )

    // RUBY_1
    // RUBY_2
    // RUBY_C
    private val mineshaftTypeRegex = scoreboardGroup.create(
        "type",
        "^\\d+/\\d+/\\d+ \\S+ (?<type>\\w{4})_(?<variant>\\w)$",
    )
    private val mineshaftFoundRegex = chatGroup.create(
        "found",
        "WOW! You found a Glacite Mineshaft portal!",
    )
    private val corpseStartRegex = chatGroup.create(
        "corpse.start",
        " +(?<type>\\w+) CORPSE LOOT! ?",
    )
    private val corpseItemRegex = chatGroup.create(
        "corpse.item",
        " +(?<item>.+?)(?: x(?<amount>[\\d,]+)|$)",
    )
    private val enchantmentItemRegex = chatGroup.create(
        "corpse.item_enchantment",
        "Enchanted Book \\((?<name>.*) (?<level>[IVX]+)\\)", // Enchanted Book (Ice Cold I)
    )
    private val corpseEndRegex = chatGroup.create(
        "corpse.end",
        "▬{64}",
    )
    private val corpseResourceful = chatGroup.create(
        "corpse.resourceful",
        "LUCKY! Your Resourceful perk saved your key from being consumed!",
    )

    public var mineshaftType: MineshaftType? = null
        private set

    public var mineshaftVariant: MineshaftVariant? = null
        private set

    public val isCrystal: Boolean
        get() = mineshaftVariant == CRYSTAL

    public var scrap: Int = 0
        private set

    public var corpses: List<Corpse> = listOf()
        private set


    private var inCorpseLoot = false
    private var consumedKey = true
    private var corpseType: CorpseType? = null
    private var corpseLoot = mutableMapOf<SkyBlockId, Int>()

    @Subscription
    @OnlyIn(MINESHAFT)
    internal fun onWidgetUpdate(event: TabWidgetChangeEvent) {
        when (event.widget) {
            AREA -> {
                scrapRegex.anyFound(event.new, "scrap") { (scrap) ->
                    MineshaftAPI.scrap = scrap.toIntValue()
                }
            }

            FROZEN_CORPSES -> {
                val foundCorpses = buildList {
                    corpseRegex.findAll(event.new, "corpse", "state") { (corpse, state) ->
                        val type = CorpseType.byName(corpse) ?: return@findAll
                        val looted = state == "LOOTED"
                        add(Corpse(type, looted))
                    }
                }
                val wasEmpty = corpses.isEmpty()
                corpses = foundCorpses
                if (wasEmpty && corpses.isNotEmpty()) {
                    //? if < 26.4
                    @Suppress("DEPRECATION") CorpseSpawnEvent(foundCorpses).post()
                    CorpseEvent.Spawn(foundCorpses).post()
                }
            }

            else -> return
        }
    }

    @Subscription
    @OnlyIn(MINESHAFT)
    internal fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        mineshaftTypeRegex.anyFound(event.added, "type", "variant") { (type, variant) ->
            this.mineshaftType = MineshaftType.fromId(type)
            this.mineshaftVariant = MineshaftVariant.fromId(variant).takeUnless { _ -> this.mineshaftType == MineshaftType.LITTLE }
            MineshaftEnteredEvent(mineshaftType, mineshaftVariant).post()
        }
    }

    @Subscription
    @OnlyIn(DWARVEN_MINES)
    internal fun onDwarvenChat(event: ChatReceivedEvent.Pre) {
        if (mineshaftFoundRegex.matches(event.text)) {
            MineshaftFoundEvent.post()
        }
    }

    @Subscription
    @OnlyIn(MINESHAFT)
    internal fun onCorpseChat(event: ChatReceivedEvent.Pre) {
        when {
            corpseStartRegex.match(event.text) -> {
                corpseStartRegex.match(event.text, "type") { (type) ->
                    inCorpseLoot = true
                    corpseLoot.clear()
                    corpseType = CorpseType.byName(type)
                }
            }

            corpseEndRegex.match(event.text) && inCorpseLoot -> {
                inCorpseLoot = false
                CorpseEvent.Looted(corpseType ?: return, corpseLoot, consumedKey).post()
                corpseLoot.clear()
                consumedKey = true
            }

            corpseResourceful.matches(event.text) -> {
                consumedKey = false
            }

            inCorpseLoot -> {
                corpseItemRegex.match(event.text) { match ->
                    val itemName = match["item"] ?: return@match
                    val count = match["amount"].toIntValue().coerceAtLeast(1)

                    val isEnchantment = enchantmentItemRegex.match(itemName, "name", "level") { (name, levelString) ->
                        val level = levelString.parseRomanOrArabic()
                        val enchantId = SkyBlockEnchantmentsRepo.getFromName(name) ?: return@match
                        corpseLoot.addOrPut(SkyBlockId.enchantment(enchantId.id, level), count)
                    }

                    if (isEnchantment) return@match

                    val id = SkyBlockId.fromName(itemName, dropLast = false) ?: return@match
                    corpseLoot.addOrPut(id, count)
                }
            }
        }
    }

    private fun reset() {
        scrap = 0
        corpses = listOf()
        mineshaftType = null
        mineshaftVariant = null
    }

    @Subscription(ServerChangeEvent::class, ServerDisconnectEvent::class)
    private fun onWorldChange() = reset()
}
