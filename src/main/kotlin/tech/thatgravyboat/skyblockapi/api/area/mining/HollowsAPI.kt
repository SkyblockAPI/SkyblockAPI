package tech.thatgravyboat.skyblockapi.api.area.mining

import me.owdding.ktmodules.Module
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.hypixel.ServerChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.location.hollows.CrystalNucleusLootedEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockEnchantmentsRepo
import tech.thatgravyboat.skyblockapi.utils.extentions.addOrPut
import tech.thatgravyboat.skyblockapi.utils.extentions.parseRomanOrArabic
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match

@Module
public object HollowsAPI {
    private val scoreboardGroup = RegexGroup.SCOREBOARD.group("mining.hollows")
    private val chatGroup = RegexGroup.CHAT.group("mining.hollows")
    private val nucleusGroup = chatGroup.group("nucleus")

    // Heat: IMMUNE
    // Heat: 24♨
    private val heatPattern = scoreboardGroup.create("heat", "Heat: (?<heat>\\d+|IMMUNE)♨?")

    private val nucleusStartRegex = nucleusGroup.create(
        "start",
        " +CRYSTAL NUCLEUS LOOT BUNDLE",
    )
    private val nucleusItemRegex = nucleusGroup.create(
        "item",
        " +(?<item>.+?)(?: x(?<amount>[\\d,]+)|$)",
    )
    private val enchantmentItemRegex = nucleusGroup.create(
        "item_enchantment",
        "Enchanted Book \\((?<name>.*) (?<level>[IVX]+)\\)", // Enchanted Book (Ice Cold I)
    )
    private val nucleusEndRegex = nucleusGroup.create(
        "end",
        "▬{64}",
    )


    public var heat: Int? = 0
        private set

    public val immuneToHeat: Boolean get() = heat == null

    private var inNucleusLoot = false
    private var nucleusLoot = mutableMapOf<SkyBlockId, Int>()

    @Subscription
    @OnlyIn(CRYSTAL_HOLLOWS)
    internal fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        val heatFound = heatPattern.anyMatch(event.added, "heat") { (heat) ->
            this.heat = heat.toIntOrNull()
        }

        if (!heatFound && this.heat != 0 && heatPattern.anyMatch(event.removed)) {
            this.heat = 0
        }
    }

    @Subscription
    @OnlyIn(CRYSTAL_HOLLOWS)
    internal fun onNucleusChat(event: ChatReceivedEvent.Pre) {
        when {
            nucleusStartRegex.match(event.text) -> {
                inNucleusLoot = true
                nucleusLoot.clear()
            }

            nucleusEndRegex.match(event.text) && inNucleusLoot -> {
                inNucleusLoot = false
                CrystalNucleusLootedEvent(nucleusLoot).post()
                nucleusLoot.clear()
            }

            inNucleusLoot -> {
                nucleusItemRegex.match(event.text) { match ->
                    val itemName = match["item"] ?: return@match
                    val count = match["amount"].toIntValue().coerceAtLeast(1)

                    val isEnchantment = enchantmentItemRegex.match(itemName, "name", "level") { (name, levelString) ->
                        val level = levelString.parseRomanOrArabic()
                        val enchantId = SkyBlockEnchantmentsRepo.getFromName(name) ?: return@match
                        nucleusLoot.addOrPut(SkyBlockId.enchantment(enchantId.id, level), count)
                    }

                    if (isEnchantment) return@match

                    val id = SkyBlockId.fromName(itemName, dropLast = false) ?: return@match
                    nucleusLoot.addOrPut(id, count)
                }
            }
        }
    }

    private fun reset() {
        heat = 0
    }

    @Subscription(ServerChangeEvent::class)
    private fun onServerChange() = reset()
}
