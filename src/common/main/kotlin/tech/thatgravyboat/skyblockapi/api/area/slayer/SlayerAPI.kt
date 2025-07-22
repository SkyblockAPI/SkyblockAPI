package tech.thatgravyboat.skyblockapi.api.area.slayer

import kotlinx.datetime.Instant
import me.owdding.ktmodules.Module
import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.RemoveNextVersion
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.entity.ComponentAttachEvent
import tech.thatgravyboat.skyblockapi.api.events.entity.NameChangedEvent
import tech.thatgravyboat.skyblockapi.api.events.entity.SlayerInfoLineAttachEvent
import tech.thatgravyboat.skyblockapi.api.events.entity.SlayerInfoLineChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.utils.extentions.parseFormattedInt
import tech.thatgravyboat.skyblockapi.utils.extentions.parseRomanNumeral
import tech.thatgravyboat.skyblockapi.utils.extentions.toFormattedString
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyFound
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.time.currentInstant
import tech.thatgravyboat.skyblockapi.utils.time.since
import java.util.*
import kotlin.time.Duration.Companion.milliseconds

@Module
object SlayerAPI {

    private val slayerBosses: WeakHashMap<Entity, SlayerInfo> = WeakHashMap()
    private val slayerGroup = RegexGroup.SCOREBOARD.group("slayer")
    private val chatSlayerGroup = RegexGroup.CHAT.group("slayer")
    private val slayerQuestRegex = slayerGroup.create("quest", "Slayer Quest")
    private val slayerTypeRegex = slayerGroup.create("type", "(?<type>[\\w ]+) (?<level>[MDCLXVI]+)")
    private val slayerAmountRegex = slayerGroup.create(
        "amount",
        " \\(?(?<amount>[\\d,]+)/(?<total>[\\d,]+)\\)? (?<dynamic>Combat XP|Kills)",
    )
    private val slayerBossTextRegex = slayerGroup.create(
        "boss",
        "(?<text>Slay the boss!|Boss slain!)",
    )
    private val questStarted = chatSlayerGroup.create("started", "\\s+SLAYER QUEST STARTED!")
    private val questCompleted = chatSlayerGroup.create("completed", "\\s+SLAYER QUEST COMPLETE!")

    var type: SlayerType? = null
        private set
    var level: Int = 0
        private set

    var text: String? = null
        private set

    @RemoveNextVersion
    val current: Int get() = progress?.current ?: 0
    @RemoveNextVersion
    val max: Int get() = progress?.max ?: 0

    var progress: SlayerProgress? = null
        private set

    var lastType: SlayerType? = null
        private set
    var lastLevel: Int = 0
        private set

    var questFinished: Instant = Instant.DISTANT_PAST

    @Subscription
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        if (event.removed.any { slayerQuestRegex.matches(it) }) {
            reset()
        } else if (type == null && level == 0) {
            val index = event.added.indexOfFirst { slayerQuestRegex.matches(it) }
            if (index != -1 && event.new.size > index) {
                slayerTypeRegex.match(event.added[index + 1], "type", "level") { (type, level) ->
                    SlayerAPI.type = SlayerType.fromDisplayName(type)
                    SlayerAPI.level = level.parseRomanNumeral()
                }
            }
        }
        // this needs to be in another if statement because both slayerTypeRegex and
        // slayerAmountRegex can be added at the same time under certain circumstances
        if (event.added.isNotEmpty()) {
            slayerAmountRegex.anyFound(event.added, "amount", "total", "dynamic") { (amount, total, dynamic) ->
                val current = amount.parseFormattedInt()
                val max = total.parseFormattedInt()
                progress = if (dynamic == "Kills") SlayerKillProgress(current, max)
                else SlayerXpProgress(current, max)
            }
            slayerBossTextRegex.anyMatch(event.added, "text") { (text) ->
                SlayerAPI.text = text
            }
        }
    }

    @Subscription
    fun onChat(event: ChatReceivedEvent.Pre) {
        matchWhen(event.text) {
            case(questStarted) {
                if (questFinished.since() < 50.milliseconds) { // we can safely assume that this is auto slayer since it's so fast.
                    level = lastLevel
                    type = lastType
                }
            }
            case(questCompleted) {
                reset()
                questFinished = currentInstant()
            }
        }
    }

    private fun reset() {
        lastType = type
        type = null
        lastLevel = level
        level = 0
        progress = null
        text = null
    }


    @Subscription
    fun onSlayerBarUpdate(event: ComponentAttachEvent) {
        val slayerInfo: SlayerInfo = if (!isSlayerLine(event.literalComponent)) {
            slayerBosses[event.attachedTo] ?: return
        } else {
            event.attachedTo?.let { slayerBosses.computeIfAbsent(it) { SlayerInfo(it) } }
        } ?: return

        event.cancel()
        SlayerInfoLineAttachEvent(event.component, event.infoLineEntity, slayerInfo).post(SkyBlockAPI.eventBus)
    }

    @Subscription
    fun onNameChangeEvent(event: NameChangedEvent) {
        event.attachedTo?.let { slayerBosses[it] }?.let {
            event.cancel()
            SlayerInfoLineChangeEvent(event.component, event.infoLineEntity, it).post(SkyBlockAPI.eventBus)
        }
    }

    private fun isSlayerLine(line: String) = line.startsWith("☠") || (line.endsWith("❤") || line.endsWith("❤ ✯"))

    @Subscription
    fun onRegisterCommands(event: RegisterCommandsEvent) {
        event.register("sbapi slayer") {
            thenCallback("progress") {
                val progress = progress
                if (progress == null) {
                    Text.sendDebug("No slayer progress found.")
                    return@thenCallback
                }
                Text.sendDebug(
                    "Slayer Progress: ${progress.current}/${progress.max}" +
                        "(${progress::class.simpleName}) [${progress.percentage.toFormattedString()}%]"
                )
            }
        }
    }
}

interface SlayerMob {
    val displayName: String

    @RemoveNextVersion
    val inGameName: String get() = displayName
    val inGameNames: List<String> get() = listOf(displayName)
}

val SLAYER_MOBS: List<SlayerMob> = listOf(
    SlayerMiniBoss.entries,
    SlayerDemon.entries,
    SlayerType.entries,
).flatMap { listOf(*it.toTypedArray<SlayerMob>()) }

enum class SlayerMiniBoss(
    override val displayName: String,
    val tier: Int,
    val slayerType: SlayerType,
    val isBigBoy: Boolean = false,
) : SlayerMob {
    REVENANT_SYCOPHANT("Revenant Sycophant", 3, SlayerType.REVENANT_HORROR),
    REVENANT_CHAMPION("Revenant Champion", 4, SlayerType.REVENANT_HORROR),
    DEFORMED_REVENANT("Deformed Revenant", 4, SlayerType.REVENANT_HORROR, true),
    ATONED_CHAMPION("Atoned Champion", 5, SlayerType.REVENANT_HORROR),
    ATONED_REVENANT("Atoned Revenant", 5, SlayerType.REVENANT_HORROR, true),

    TARANTULA_VERMIN("Tarantula Vermin", 3, SlayerType.TARANTULA_BROODFATHER),
    TARANTULA_BEAST("Tarantula Beast", 4, SlayerType.TARANTULA_BROODFATHER),
    MUTANT_TARANTULA("Mutant Tarantula", 4, SlayerType.TARANTULA_BROODFATHER, true),
    PRIMORDIAL_JOCKEY("Primordial Jockey", 5, SlayerType.TARANTULA_BROODFATHER),
    PRIMORDIAL_VISCOUNT("Primordial Viscount", 5, SlayerType.TARANTULA_BROODFATHER, true),

    PACK_ENFORCER("Pack Enforcer", 3, SlayerType.SVEN_PACKMASTER),
    SVEN_FOLLOWER("Sven Follower", 4, SlayerType.SVEN_PACKMASTER),
    SVEN_ALPHA("Sven Alpha", 4, SlayerType.SVEN_PACKMASTER, true),

    VOIDLING_DEVOTEE("Voidling Devotee", 3, SlayerType.VOIDGLOOM_SERAPH),
    VOIDLING_RADICAL("Voidling Radical", 4, SlayerType.VOIDGLOOM_SERAPH),
    VOIDCRAZED_MANIAC("Voidcrazed Maniac", 4, SlayerType.VOIDGLOOM_SERAPH, true),

    FLARE_DEMON("Flare Demon", 3, SlayerType.INFERNO_DEMONLORD),
    KINDLEHEART_DEMON("Kindleheart Demon", 4, SlayerType.INFERNO_DEMONLORD),
    BURNINGSOUL_DEMON("Burningsoul Demon", 4, SlayerType.INFERNO_DEMONLORD, true),
}

enum class SlayerDemon(override val displayName: String, val slayerType: SlayerType) : SlayerMob {
    QUAZII("ⓆⓊⒶⓏⒾⒾ", SlayerType.INFERNO_DEMONLORD),
    TYPHOEUS("ⓉⓎⓅⒽⓄⒺⓊⓈ", SlayerType.INFERNO_DEMONLORD)
}

enum class SlayerType(override val displayName: String, val otherName: String) : SlayerMob {
    REVENANT_HORROR("Revenant Horror", "Zombie") {
        override val inGameNames = listOf("Revenant Horror", "Atoned Horror")
    },
    TARANTULA_BROODFATHER("Tarantula Broodfather", "Spider"),
    SVEN_PACKMASTER("Sven Packmaster", "Wolf"),
    VOIDGLOOM_SERAPH("Voidgloom Seraph", "Enderman"),
    RIFTSTALKER_BLOODFIEND("Riftstalker Bloodfiend", "Vampire") {
        override val inGameNames = listOf("Bloodfiend")
    },
    INFERNO_DEMONLORD("Inferno Demonlord", "Blaze"),
    ;

    val apiName = otherName.lowercase()

    companion object {
        fun fromDisplayName(displayName: String): SlayerType? = entries.find {
            it.displayName.equals(displayName, ignoreCase = true)
        }

        fun fromName(name: String): SlayerType? = entries.find { it.otherName.equals(name, ignoreCase = true) } ?: fromDisplayName(name)
    }
}
