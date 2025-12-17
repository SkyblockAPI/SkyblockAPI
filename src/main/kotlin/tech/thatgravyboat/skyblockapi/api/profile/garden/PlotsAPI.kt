package tech.thatgravyboat.skyblockapi.api.profile.garden

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Items
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import tech.thatgravyboat.skyblockapi.api.data.stored.PlotsStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.*
import tech.thatgravyboat.skyblockapi.api.events.chat.ChatReceivedEvent
import tech.thatgravyboat.skyblockapi.api.events.info.ScoreboardUpdateEvent
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidget
import tech.thatgravyboat.skyblockapi.api.events.info.TabWidgetChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McPlayer.contains
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.Destructured
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.matchOrNull
import tech.thatgravyboat.skyblockapi.utils.regex.matchWhen
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix
import kotlin.math.floor

@Module
object PlotAPI {
    //region Regex
    private val inventoryGroup = RegexGroup.INVENTORY.group("plots")
    private val scoreboardGroup = RegexGroup.SCOREBOARD.group("plots")
    private val tablistGroup = RegexGroup.TABLIST.group("plots")
    private val chatGroup = RegexGroup.CHAT.group("plots")

    private val deskPlotNameRegex = inventoryGroup.create(
        "name",
        "Plot - (?<name>.+)",
    )

    private val deskPestsRegex = inventoryGroup.create(
        "pests",
        "ൠ This plot has (?<amount>\\d+) ൠ Pests?!",
    )

    private val scoreboardPestAmountRegex = scoreboardGroup.create(
        "pest_amount",
        " ⏣ The Garden ൠ x(?<amount>\\d+)",
    )

    private val scoreboardNoPestsRegex = scoreboardGroup.create(
        "no_pests",
        " ⏣ (?:The Garden|Plot - .+)",
    )

    private val scoreboardPlotPestAmountRegex = scoreboardGroup.create(
        "plot_pest_amount",
        " {3}Plot - (?<name>.+) ൠ x(?<amount>\\d+)",
    )

    private val tablistAliveRegex = tablistGroup.create(
        "pests_alive",
        "\\s*Alive: (?<amount>\\d+)",
    )

    private val tablistPlotsRegex = tablistGroup.create(
        "plots",
        "\\s*Plots: (?<plots>(?:\\d+,?\\s*)+)",
    )

    private val chatSingularSpawnRegex = chatGroup.create(
        "singular_spawn",
        "[^:]*! A ൠ Pest has appeared in Plot - (?<name>.+)!",
    )
    private val chatPluralSpawnRegex = chatGroup.create(
        "plural_spawn",
        "[^:]+! (?<amount>\\d+) ൠ Pests? have spawned in Plot - (?<name>.+)!",
    )
    private val chatOfflineSpawnRegex = chatGroup.create(
        "offline",
        "[^:]+! While you were offline, ൠ Pests? spawned in Plots (?<plots>.*)!",
    )

    //endregion

    private var currentPlotSlot = 1

    val plots = listOf(
        listOf(21, 13, 9, 14, 22),
        listOf(15, 5, 1, 6, 16),
        listOf(10, 2, 0, 3, 11),
        listOf(17, 7, 4, 8, 18),
        listOf(23, 19, 12, 20, 24),
    ).withIndex().flatMap { (z, row) ->
        row.withIndex().map { (x, id) ->
            val minX = x * 96 - 240.0
            val minZ = z * 96 - 240.0
            val maxX = x * 96 - 144.0
            val maxZ = z * 96 - 144.0
            val aabb = AABB(minX, 0.0, minZ, maxX, 256.0, maxZ)
            currentPlotSlot++
            Plot(id, currentPlotSlot, aabb)
        }.also { currentPlotSlot += 4 }
    }

    var currentPestAmount = 0
        private set


    fun getPlot(id: Int): Plot? = plots.find { it.id == id }
    fun getPlotByName(name: String): Plot? = plots.find { it.data?.name == name }

    fun getPlot(pos: Vec3): Plot? = plots.find { pos in it.aabb }
    fun getPlot(pos: BlockPos): Plot? = getPlot(Vec3(pos))
    fun getCurrentPlot(): Plot? = McPlayer.position?.let(::getPlot)


    private fun clearPests() {
        currentPestAmount = 0
        plots.forEach { plot ->
            plot.data?.pest = Pest(0, inaccurate = false)
            plot.data?.save()
        }
    }

    private fun synch() {
        if (plots.none { it.data?.pest?.inaccurate == true }) return
        val accurateAmount = plots.mapNotNull { it.data?.pest }.filterNot { it.inaccurate }.sumOf { it.pest }
        val inaccuratePlots = plots.mapNotNull { it.data?.pest }.filter { it.inaccurate }

        if (inaccuratePlots.size == 1) {
            val pest = inaccuratePlots.first()
            pest.pest = currentPestAmount - accurateAmount
            pest.inaccurate = false
            plots.find { it.data?.pest == pest }?.data?.save()
        } else if (currentPestAmount == accurateAmount + inaccuratePlots.sumOf { it.pest }) {
            inaccuratePlots.forEach { pest ->
                pest.inaccurate = false
                plots.find { it.data?.pest == pest }?.data?.save()
            }
        }
    }

    @Subscription
    @OnlyNonGuest
    @OnlyIn(SkyBlockIsland.GARDEN)
    fun onScoreboardUpdate(event: ScoreboardUpdateEvent) {
        if (scoreboardNoPestsRegex.anyMatch(event.new)) {
            clearPests()
            return
        }

        var shouldSynch = false
        scoreboardPestAmountRegex.anyMatch(event.new, "amount") { (amount) ->
            currentPestAmount = amount.toIntValue()
            shouldSynch = true
        }

        scoreboardPlotPestAmountRegex.anyMatch(event.new, "name", "amount") { (name, amount) ->
            val plot = plots.find { it.data?.name == name } ?: return@anyMatch
            val pest = Pest(amount.toIntValue(), inaccurate = false)
            plot.data?.pest = pest
            plot.data?.save()
            shouldSynch = true
        }

        if (shouldSynch) synch()
    }

    @Subscription
    @OnlyNonGuest
    @OnlyIn(SkyBlockIsland.GARDEN)
    @MustBeContainer
    @InventoryTitle("Configure Plots")
    private fun InventoryChangeEvent.onInventoryChange() {
        val plotId = plots.find { it.slot == slot.index }?.id ?: return
        val itemId = item.getSkyBlockId() ?: item.getItemModel().takeUnless { it == Items.OAK_BUTTON || it in ItemTag.GLASS_PANES }?.let {
            BuiltInRegistries.ITEM.getKey(it.asItem()).toString()
        }

        val name = deskPlotNameRegex.matchOrNull(item.cleanName, "name", action = Destructured::component1) ?: ""
        var pests = 0
        deskPestsRegex.anyMatch(item.getRawLore(), "amount") { (amount) ->
            pests = amount.toIntValue()
        }
        val locked = item.getRawLore().any { it == "Cost:" }

        val plot = PlotData(
            plotId,
            name,
            Pest(pests, inaccurate = false),
            itemId,
            locked,
        )
        plot.save()
    }

    @Subscription
    @OnlyNonGuest
    @OnlyIn(SkyBlockIsland.GARDEN)
    fun onChat(event: ChatReceivedEvent.Pre) {
        val shouldSynch = matchWhen(event.text) {
            case(chatSingularSpawnRegex, "name") { (name) ->
                val plot = getPlotByName(name) ?: return@case
                plot.data?.pest?.let {
                    it.pest += 1
                    plot.data?.save()
                }
            }
            case(chatPluralSpawnRegex, "amount", "name") { (amount, name) ->
                val plot = getPlotByName(name) ?: return@case
                plot.data?.pest?.let {
                    it.pest += amount.toIntValue()
                    plot.data?.save()
                }
            }
            case(chatOfflineSpawnRegex, "plots") { (plotsLiteral) ->
                val plots = mutableListOf<String>()
                plotsLiteral.split(", ", " and ").forEach { plotName ->
                    plots.add(plotName)
                }

                if (plots.isEmpty()) return@case

                plots.mapNotNull { plotName -> getPlotByName(plotName)?.data }.forEach { plot ->
                    plot.pest.inaccurate = true
                    plot.pest.pest += 1
                    plot.save()
                }
            }
        }

        if (shouldSynch) synch()
    }

    @Subscription
    @OnlyNonGuest
    @OnlyWidget(TabWidget.PESTS)
    fun onTabWidget(event: TabWidgetChangeEvent) {
        val plots = mutableListOf<Int>()
        var alive = 0

        tablistPlotsRegex.anyMatch(event.new, "plots") { (plotsLiteral) ->
            plotsLiteral.split(",").map { it.trim() }.forEach { plotId ->
                plotId.toIntValue().takeUnless { it <= 0 }?.let { plots.add(it) }
            }
        }

        tablistAliveRegex.anyMatch(event.new, "amount") { (aliveLiteral) ->
            alive = aliveLiteral.toIntValue()
        }

        val accuratePets = plots.mapNotNull { PlotsStorage.getPlot(it)?.pest }.filterNot { it.inaccurate }.sumOf { it.pest }
        val amountPerPlot = floor(alive / plots.size.toFloat()).toInt()
        alive -= accuratePets

        plots.removeIf {
            val pests = PlotsStorage.getPlot(it)?.pest
            (pests?.inaccurate == false) && (pests.pest) >= amountPerPlot
        }

        if (alive == 0 || plots.isEmpty()) return
        val actualAmountPerPlot = floor(alive / plots.size.toFloat()).toInt()


        plots.map { PlotsStorage.plots.find { plot -> plot.id == it } ?: PlotData(it, Pest(0, true), null, false) }
            .forEach { plot ->
                val pest = plot.pest
                pest.pest = actualAmountPerPlot
                pest.inaccurate = true
                plot.save()
            }

        synch()
    }


    @Subscription
    fun onCommand(event: RegisterCommandsEvent) {
        event.register("sbapi garden plots") {
            thenCallback("clear") {
                PlotsStorage.clear()
                Text.of("Cleared all plots!").sendWithPrefix()
            }

            callback {
                val string = buildList {
                    add("Plots:")
                    PlotsStorage.plots.forEach { plot ->
                        val pest = plot.pest
                        val pestText = if (pest.inaccurate) " (Inacc)" else ""
                        add("  - Plot ${plot.id}: ${plot.name}, Pests: ${pest.pest}$pestText, Locked: ${plot.locked}, Icon: ${plot.deskIcon ?: "None"}")
                    }
                }

                McClient.clipboard = string.joinToString("\n")
                Text.multiline(string).sendWithPrefix()
            }
        }
    }

}

data class Plot(
    val id: Int,
    val slot: Int,
    val aabb: AABB,
) {
    val isBarn = id == 0
    val tpName = if (isBarn) "barn" else id
    val data get() = PlotsStorage.getPlot(id)
}

@GenerateCodec
data class PlotData(
    val id: Int,
    var name: String,
    var pest: Pest,
    var deskIcon: String?,
    var locked: Boolean = false,
) {
    constructor(id: Int, pest: Pest, deskIcon: String? = null, locked: Boolean = false) : this(id, "$id", pest, deskIcon, locked)

    internal fun save() {
        PlotsStorage.setPlot(this)
    }
}

@GenerateCodec
data class Pest(
    var pest: Int,
    var inaccurate: Boolean,
)
