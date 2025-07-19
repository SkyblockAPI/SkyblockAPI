package tech.thatgravyboat.skyblockapi.api.data.stored

import tech.thatgravyboat.skyblockapi.api.data.StoredProfileData
import tech.thatgravyboat.skyblockapi.api.profile.garden.PlotData
import tech.thatgravyboat.skyblockapi.generated.CodecUtils
import tech.thatgravyboat.skyblockapi.generated.SkyblockAPICodecs

internal object PlotsStorage {

    private val PLOTS = StoredProfileData(
        { mutableListOf() },
        CodecUtils.mutableList(SkyblockAPICodecs.PlotDataCodec.codec()),
        "plots.json",
    )

    var plots: MutableList<PlotData>
        get() = PLOTS.get() ?: mutableListOf()
        private set(value) {
            val data = PLOTS.get().takeUnless { it == value } ?: return
            data.clear()
            data.addAll(value)
            PLOTS.save()
        }

    fun getPlot(id: Int): PlotData? = plots.find { it.id == id }

    fun setPlot(plotToStore: PlotData) {
        val plot = plots.find { it.id == plotToStore.id }
        if (plot == plotToStore) return

        if (plot != null) {
            plots.remove(plot)
        }
        plots.add(plotToStore)

        PLOTS.save()
    }

    fun clear() {
        plots.clear()
        PLOTS.save()
    }

}
