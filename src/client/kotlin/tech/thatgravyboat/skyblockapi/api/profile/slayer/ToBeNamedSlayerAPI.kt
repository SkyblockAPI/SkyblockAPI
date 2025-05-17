package tech.thatgravyboat.skyblockapi.api.profile.slayer

import tech.thatgravyboat.skyblockapi.api.area.slayer.SlayerType
import tech.thatgravyboat.skyblockapi.api.data.stored.SlayerStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyOnSkyBlock
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvOpenedEvent
import tech.thatgravyboat.skyblockapi.api.events.remote.SkyBlockPvRequired
import tech.thatgravyboat.skyblockapi.api.remote.repo.RepoSlayerData
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.asLong
import tech.thatgravyboat.skyblockapi.utils.extentions.asMap
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.extentions.toLongValue
import tech.thatgravyboat.skyblockapi.utils.json.getPath
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen

@Module
object ToBeNamedSlayerAPI {
    private var lastType: SlayerType? = null

    val group = RegexGroup.CHAT.group("slayer")
    val chatXpRegex = group.create("xp", "\\s+(?<type>.*) Slayer LVL (?<level>\\d+) - Next LVL in (?<xp>[\\d.,]+) XP!")
    val chatXpMaxedRegex = group.create("maxedXp", "\\s+(?<type>.*) Slayer LVL (?<maxedLevel>\\d+) - LVL MAXED OUT!")
    val chatMeterRegex = group.create("meter", "\\s+RNG Meter - (?<xp>[\\d.,]+) Stored XP")

    val slayerData: Map<SlayerType, SlayerEntry> get() = SlayerStorage.data

    @Subscription
    @OnlyOnSkyBlock
    fun onChat(event: ChatReceivedEvent.Post) {
        val found = matchWhen(event.text) {
            case(chatXpRegex, "type", "level", "xp") { (type, level, xp) ->
                lastType = SlayerType.fromName(type)
                lastType?.let { type ->
                    val xpRequiredForNext = RepoSlayerData.getData(type).leveling[level.toIntValue()]
                    SlayerStorage.setXp(type, xpRequiredForNext - xp.toLongValue())
                }
            }
            case(chatXpMaxedRegex, "type", "maxedLevel") { (type, maxedLevel) ->
                // TODO: track xp over max lvl
                lastType = SlayerType.fromName(type)
                lastType?.let { SlayerStorage.setXp(it, RepoSlayerData.getData(it).leveling.max()) }

            }
            case(chatMeterRegex, "xp") { (meterXp) ->
                lastType?.let { SlayerStorage.setMeter(it, meterXp.toLongValue()) }
            }
        }

        if (!found) lastType = null
    }

    @Subscription
    @OnlyOnSkyBlock
    @OptIn(SkyBlockPvRequired::class)
    fun onPv(event: SkyBlockPvOpenedEvent) {
        event.member.getPath("slayer.slayer_bosses").asMap { k, v ->
            SlayerType.fromName(k) to v.asJsonObject["xp"].asLong(0)
        }.filterKeys { it != null }.mapKeys { it.key!! }.forEach { t, u ->
            SlayerStorage.setXp(t, u)
        }
    }
}
