package tech.thatgravyboat.skyblockapi.api.area.slayer

import net.minecraft.world.entity.Entity
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.entity.ComponentAttachEvent
import tech.thatgravyboat.skyblockapi.api.events.entity.NameChangedEvent
import tech.thatgravyboat.skyblockapi.api.events.entity.SlayerInfoLineAttachEvent
import tech.thatgravyboat.skyblockapi.api.events.entity.SlayerInfoLineChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.modules.Module
import tech.thatgravyboat.skyblockapi.utils.extentions.parseRomanNumeral
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import java.util.WeakHashMap

@Module
object SlayerAPI {

    private val slayerBosses: WeakHashMap<Entity, SlayerInfo> = WeakHashMap()
    private val slayerGroup = RegexGroup.SCOREBOARD.group("slayer")
    private val slayerQuestRegex = slayerGroup.create("quest", "Slayer Quest")
    private val slayerTypeRegex = slayerGroup.create("type", "(?<type>[\\w ]+) (?<level>[MDCLXVI]+)")
    private val slayerAmountRegex = slayerGroup.create(
        "amount",
        " \\(?(?<amount>[\\d,]+)/(?<total>[\\d,]+)\\)? (Combat XP|Kills)",
    )
    private val slayerBossTextRegex = slayerGroup.create(
        "boss",
        "(?<text>Slay the boss!|Boss slain!)",
    )

    var type: SlayerType? = null
        private set
    var level: Int = 0
        private set

    var text: String? = null
        private set
    var current: Int = 0
        private set
    var max: Int = 0
        private set

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
        } else if (event.added.isNotEmpty()) {
            slayerAmountRegex.anyMatch(event.added, "amount", "total") { (amount, total) ->
                current = amount.toIntValue()
                max = total.toIntValue()
            }
            slayerBossTextRegex.anyMatch(event.added, "text") { (text) ->
                SlayerAPI.text = text
            }
        }
    }

    private fun reset() {
        type = null
        text = null
        level = 0
        current = 0
        max = 0
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
}

interface SlayerMob {
    val displayName: String
    val inGameName: String get() = displayName
}

val SLAYER_MOBS: List<SlayerMob> = listOf(
    SlayerMiniBoss.entries,
    SlayerDemon.entries,
    SlayerType.entries,
).flatMap { listOf(*it.toTypedArray()) }

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

enum class SlayerType(override val displayName: String) : SlayerMob {
    REVENANT_HORROR("Revenant Horror"),
    TARANTULA_BROODFATHER("Tarantula Broodfather"),
    SVEN_PACKMASTER("Sven Packmaster"),
    VOIDGLOOM_SERAPH("Voidgloom Seraph"),
    RIFTSTALKER_BLOODFIEND("Riftstalker Bloodfiend") {
        override val inGameName get() = "Bloodfiend"
    },
    INFERNO_DEMONLORD("Inferno Demonlord"),
    ;

    companion object {
        fun fromDisplayName(displayName: String): SlayerType? = entries.firstOrNull {
            it.displayName.equals(displayName, ignoreCase = true)
        }
    }
}
