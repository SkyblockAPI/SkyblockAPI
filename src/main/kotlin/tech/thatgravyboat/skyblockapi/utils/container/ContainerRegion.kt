package tech.thatgravyboat.skyblockapi.utils.container

import net.minecraft.util.Util
import net.minecraft.world.inventory.Slot
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugAttachable
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addStringDebug
import java.util.function.BiFunction

data class ContainerRegion(
    val columns: IntRange,
    val rows: IntRange,
    val contentFlow: ContentFlow = ContentFlow.topLeft().rows()
) {
    companion object : ItemDebugCategory {
        override fun toString(): String = "Container Region"
    }

    val rowSpan = rows.span()
    val columnSpan = columns.span()
    val rowStart = rows.first
    val columnStart = columns.first
    private fun IntRange.span() = (last - first) + 1

    val size = rowSpan * columnSpan

    fun getId(slot: Slot, page: Int = 0, contentFlow: ContentFlow = this.contentFlow, category: ItemDebugCategory = ContainerRegion, attachable: ItemDebugAttachable = slot.item): Int? {
        if (!contains(slot)) {
            attachable.addStringDebug(category) { "Not in region!" }
            return null
        }
        val offset = (page - 1) * size

        val row = slot.index / 9 - rowStart
        val column = slot.index % 9 - columnStart

        return contentFlow.index(rowSpan, columnSpan, row, column) {
            attachable.addStringDebug(category) { it }
        } + offset
    }

    operator fun contains(slot: Slot): Boolean {
        val row = slot.index / 9
        val column = slot.index % 9
        return row in rows && column in columns
    }
}

enum class Anchor(val invertColumns: Boolean, val invertRows: Boolean) {
    TOP_LEFT(false, false),
    TOP_RIGHT(true, false),
    BOTTOM_LEFT(false, true),
    BOTTOM_RIGHT(true, true),
    ;

    fun row(rows: Int, row: Int): Int = if (invertRows) rows - row - 1 else row
    fun column(columns: Int, column: Int): Int = if (invertColumns) columns - column - 1 else column
}

enum class ContentDirection {
    Horizontal {
        override fun index(rows: Int, columns: Int, row: Int, column: Int, debugCollector: ((String) -> Unit)?) : Int {
            debugCollector?.invoke("row: $row, column: $column")
            return columns * row + column
        }
    },
    Vertical {
        override fun index(rows: Int, columns: Int, row: Int, column: Int, debugCollector: ((String) -> Unit)?) : Int {
            debugCollector?.invoke("row: $row, column: $column")
            return rows * column + row
        }
    },
    ;

    abstract fun index(rows: Int, columns: Int, row: Int, column: Int, debugCollector: ((String) -> Unit)? = null) : Int
}

@ConsistentCopyVisibility
data class ContentFlow private constructor(val anchor: Anchor, val direction: ContentDirection) {

    fun index(rows: Int, columns: Int, row: Int, column: Int, debugCollector: ((String) -> Unit)? = null) : Int {
        debugCollector?.invoke("$row:$column / $rows:$columns")
        debugCollector?.invoke("$anchor:$direction".lowercase())
        val index = direction.index(rows, columns, anchor.row(rows, row), anchor.column(columns, column), debugCollector)
        debugCollector?.invoke("Index: $index")
        return index
    }

    companion object {
        private val cache: BiFunction<Anchor, ContentDirection, ContentFlow> = Util.memoize { anchor, direction ->
            ContentFlow(anchor, direction)
        }

        fun interface FlowBuilder {
            fun anchor(): Anchor
            fun rows(): ContentFlow = cache.apply(anchor(), ContentDirection.Horizontal)
            fun columns(): ContentFlow = cache.apply(anchor(), ContentDirection.Vertical)
        }


        fun topLeft() : FlowBuilder = { Anchor.TOP_LEFT }
        fun topRight() : FlowBuilder = { Anchor.TOP_RIGHT }
        fun bottomLeft() : FlowBuilder = { Anchor.BOTTOM_LEFT }
        fun bottomRight() : FlowBuilder = { Anchor.BOTTOM_RIGHT }
    }
}
