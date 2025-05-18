package tech.thatgravyboat.skyblockapi.api.profile.slayer

import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerAPI
import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.api.data.Perk
import tech.thatgravyboat.skyblockapi.api.data.stored.SlayerStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.MustBeContainer
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvOpenedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvRequired
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.remote.repo.RepoSlayerData
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.getPath
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.findWhen
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen

@Module
object SlayerProgressAPI {
    private var lastType: SlayerType? = null

    val chatGroup = RegexGroup.CHAT.group("slayer")
    val inventoryGroup = RegexGroup.INVENTORY.group("slayer")
    val chatXpRegex = chatGroup.create("xp", "\\s+(?<type>.*) Slayer LVL (?<level>\\d+) - Next LVL in (?<xp>[\\d.,]+) XP!")
    val chatXpMaxedRegex = chatGroup.create("maxedXp", "\\s+(?<type>.*) Slayer LVL (?<maxedLevel>\\d+) - LVL MAXED OUT!")
    val chatMeterRegex = chatGroup.create("meter", "\\s+RNG Meter - (?<xp>[\\d.,]+) Stored XP")
    val inventoryXpRegex = inventoryGroup.create("xp", "\\s*(?<type>.*) XP to LVL (?<lvl>\\d):\\n\\s*(?<xp>[\\d,]+)/.*?")
    val inventoryMaxedRegex = inventoryGroup.create("maxedXp", "\\s*Reached max level!")
    val inventoryRngSelected = inventoryGroup.create("rngSelected", "Progress: [\\d.]+%\\n\\s*(?<stored>[\\d,]+)/.*?")
    val inventoryRngNonSelected = inventoryGroup.create("rngNonSelected", "Stored Slayer XP: (?<stored>[\\d,]+)")

    val slayerData: Map<SlayerType, SlayerEntry> get() = SlayerStorage.data

    @Subscription
    @OnlyOnSkyBlock
    fun onChat(event: ChatReceivedEvent.Pre) {
        val found = matchWhen(event.text) {
            case(chatXpRegex, "type", "level", "xp") { (type, level, xp) ->
                lastType = SlayerType.fromName(type)
                lastType?.let { type ->
                    val xpRequiredForNext = RepoSlayerData.getData(type).leveling[level.toIntValue()]
                    SlayerStorage.setXp(type, xpRequiredForNext - xp.toLongValue())
                }
            }
            case(chatXpMaxedRegex, "type", "maxedLevel") { (type, _) ->
                lastType = SlayerType.fromName(type)
                lastType?.let {
                    val slayerData = RepoSlayerData.getData(it)
                    val lastXp = SlayerStorage.getXp(it).coerceAtLeast(slayerData.leveling.max())
                    val gain = (slayerData.bossXp.getOrNull(SlayerAPI.level - 1) ?: 0).let { xp ->
                        if (Perk.SLAYER_XP_BUFF.active) (xp * 1.25).toInt() else xp
                    }
                    SlayerStorage.setXp(it, lastXp + gain)
                }
            }
            case(chatMeterRegex, "xp") { (meterXp) ->
                lastType?.let { SlayerStorage.setMeter(it, meterXp.toLongValue()) }
            }
        }

        if (!found) lastType = null
    }

    @Subscription
    @OnlyOnSkyBlock
    @MustBeContainer
    fun onInventory(event: InventoryChangeEvent) {
        if (!SlayerType.entries.map { it.displayName }.contains(event.title)) return
        val slayerType = SlayerType.entries.find { it.displayName == event.title } ?: return

        //Text.of(event.slot.item.hoverName.stripped).send()
        when (event.slot.index) {
            28 -> findWhen(event.slot.item.getRawLore().joinToString("\n")) {
                case(inventoryXpRegex, "xp") { (xp) ->
                    SlayerStorage.setXp(slayerType, xp.toLongValue())
                }
                case(inventoryMaxedRegex) { _ ->
                    SlayerStorage.setXp(slayerType, RepoSlayerData.getData(slayerType).leveling.max())
                }
            }

            34 -> findWhen(event.slot.item.getRawLore().joinToString("\n")) {
                case(inventoryRngSelected, "stored") { (stored) ->
                    SlayerStorage.setMeter(slayerType, stored.toLongValue())
                }
                case(inventoryRngNonSelected, "stored") { (stored) ->
                    SlayerStorage.setMeter(slayerType, stored.toLongValue())
                }
            }
        }
    }

    @Subscription
    @OnlyOnSkyBlock
    @OptIn(SkyBlockPvRequired::class)
    fun onPv(event: SkyBlockPvOpenedEvent) {
        event.member.getPath("slayer.slayer_bosses").asMap { k, v ->
            SlayerType.fromName(k) to v.asJsonObject["xp"].asLong(0)
        }.filterKeys { it != null }.mapKeys { it.key!! }.forEach(SlayerStorage::setXp)
    }
}
