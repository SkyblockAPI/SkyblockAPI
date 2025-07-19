package tech.thatgravyboat.skyblockapi.api.profile.garden

import me.owdding.ktcodecs.GenerateCodec
import me.owdding.ktmodules.Module
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.Items
import net.minecraft.world.phys.AABB
import tech.thatgravyboat.skyblockapi.api.data.stored.PlotsStorage
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.InventoryTitle
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.MustBeContainer
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnlyIn
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.TimePassed
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.InventoryChangeEvent
import tech.thatgravyboat.skyblockapi.api.events.time.TickEvent
import tech.thatgravyboat.skyblockapi.api.location.SkyBlockIsland
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McLevel
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.helpers.McPlayer.contains
import tech.thatgravyboat.skyblockapi.impl.tagkey.ItemTag
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.regex.RegexGroup
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.anyMatch
import tech.thatgravyboat.skyblockapi.utils.regex.RegexUtils.match
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.Text.sendWithPrefix

@Module
object PlotAPI {
    //region Regex
    private val inventoryGroup = RegexGroup.INVENTORY.group("plots")

    private val deskPlotNameRegex = inventoryGroup.create(
        "name",
        "Plot - (?<name>.+)",
    )

    private val deskPestsRegex = inventoryGroup.create(
        "pests",
        "ൠ This plot has (?<amount>\\d+) Pests?!",
    )

    //endregion

    private var slot = 1

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
            slot++
            Plot(id, slot, aabb)
        }.also { slot += 4 }
    }

    fun getCurrentPlot(): Plot? = plots.firstOrNull { McPlayer in it.aabb }

    @Subscription
    @InventoryTitle("Configure Plots")
    @MustBeContainer
    private fun InventoryChangeEvent.onInventoryChange() {
        val plotId = plots.find { it.slot == slot.index }?.id ?: return
        val itemId = item.getSkyBlockId() ?: item.getItemModel().takeUnless { it == Items.OAK_BUTTON || it in ItemTag.GLASS_PANES }?.let {
            BuiltInRegistries.ITEM.getKey(it.asItem()).toString()
        }

        var name = ""
        deskPlotNameRegex.match(item.cleanName, "name") { (plotName) ->
            name = plotName
        }
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
    @OnlyIn(SkyBlockIsland.GARDEN)
    @TimePassed("1s")
    fun onTick(event: TickEvent) {
        if (McLevel.hasLevel) getCurrentPlot()?.id?.toString()?.let { Text.of(it).send() }
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
    internal fun save() {
        PlotsStorage.setPlot(this)
    }
}

@GenerateCodec
data class Pest(
    var pest: Int,
    var inaccurate: Boolean,
)
