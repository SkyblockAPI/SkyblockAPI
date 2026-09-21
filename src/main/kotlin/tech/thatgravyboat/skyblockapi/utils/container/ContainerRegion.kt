package tech.thatgravyboat.skyblockapi.utils.container

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.util.Util
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugAttachable
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addStringDebug
import tech.thatgravyboat.skyblockapi.utils.extentions.containerWidth
import java.util.function.BiFunction

data class ContainerRegion(
    val columns: IntRange,
    val rows: IntRange,
    @IntroducedAt("4.2.26")
    val pagesRange: IntRange = 1..Int.MAX_VALUE,
    val contentFlow: ContentFlow = ContentFlow.topLeft().rows()
) : Iterable<ContainerPosition> {
    constructor(
        width: Int,
        height: Int,
        startRow: Int = 0,
        startColumn: Int = 0,
        pagesRange: IntRange = 1..Int.MAX_VALUE,
        contentFlow: ContentFlow = ContentFlow.topLeft().rows()
    ) : this(startColumn..<startColumn + width, startRow..<startRow + height, pagesRange, contentFlow)
    companion object : ItemDebugCategory {
        override fun toString(): String = "Container Region"
    }

    init {
        require(!columns.isEmpty()) { "Columns cannot be empty!" }
        require(!rows.isEmpty()) { "Rows cannot be empty!" }
        require(!pagesRange.isEmpty()) { "Page range cannot be empty" }
    }

    val rowSpan = rows.span()
    val columnSpan = columns.span()
    val rowStart = rows.first
    val columnStart = columns.first
    private fun IntRange.span() = (last - first) + 1
    private fun pageOffset(page: Int): Int = (page.coerceIn(pagesRange) - pagesRange.first) * size

    val size = rowSpan * columnSpan

    fun getId(slot: Slot, page: Int = 1, contentFlow: ContentFlow = this.contentFlow, category: ItemDebugCategory = ContainerRegion, attachable: ItemDebugAttachable = slot.item): Int? {
        if (!contains(slot)) {
            attachable.addStringDebug(category) { "Not in region!" }
            return null
        }
        val offset = pageOffset(page)

        val row = slot.index / 9 - rowStart
        val column = slot.index % 9 - columnStart

        return contentFlow.index(rowSpan, columnSpan, row, column) {
            attachable.addStringDebug(category) { it }
        } + offset
    }

    /**
     *  Should only be iterated on once per container initialization, preferably on [ContainerInitializedEvent](tech.thatgravyboat.skyblockapi.api.events.screen.ContainerInitializedEvent).
     *
     *  If iterated on multiple times in the same container, debug strings will be added multiple times
     */
    context(category: ItemDebugCategory)
    fun iterateSlots(
        screen: AbstractContainerScreen<*>,
        page: Int = 1,
    ): Iterator<Pair<ContainerPosition, ItemStack>> = iterator {
        val slots = screen.menu.slots.takeWhile { it.container !is Inventory }.associateBy { it.index }
        for (pos in iterator(page = page)) {
            val (row, column, index, page) = pos
            val slot = slots[row * screen.containerWidth + column] ?: continue
            val item = slot.item
            fun debugCollect(string: String) = item.addStringDebug(category) { string }
            debugCollect("$row:$column / $rows:$columns")
            debugCollect("${contentFlow.anchor}:${contentFlow.direction}".lowercase())
            debugCollect("Index: $index (Page: $page)")
            yield(pos to item)
        }
    }

    operator fun contains(index: Int): Boolean {
        val row = index / 9
        val column = index % 9
        return row in rows && column in columns
    }

    operator fun contains(slot: Slot): Boolean = contains(slot.index)

    override fun iterator(): Iterator<ContainerPosition> = this.contentFlow.iterator(this.rowSpan, this.columnSpan, rowStart, columnStart)
    fun iterator(page: Int = 0) = iterator {
        contentFlow.iterator(rowSpan, columnSpan, rowStart, columnStart).forEach {
            yield(it.apply {
                this.page = page
                this.index += pageOffset(page)
            })
        }
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

        override fun column(index: Int, rows: Int, columns: Int): Int = index % columns
        override fun row(index: Int, rows: Int, columns: Int): Int = (index - column(index, rows, columns)) / columns
    },
    Vertical {
        override fun index(rows: Int, columns: Int, row: Int, column: Int, debugCollector: ((String) -> Unit)?) : Int {
            debugCollector?.invoke("row: $row, column: $column")
            return rows * column + row
        }

        override fun row(index: Int, rows: Int, columns: Int): Int =  index % rows
        override fun column(index: Int, rows: Int, columns: Int): Int = (index - row(index, rows, columns)) / rows
    },
    ;

    abstract fun row(index: Int, rows: Int, columns: Int): Int
    abstract fun column(index: Int, rows: Int, columns: Int): Int
    abstract fun index(rows: Int, columns: Int, row: Int, column: Int, debugCollector: ((String) -> Unit)? = null) : Int
}

data class ContainerPosition(
    val row: Int,
    val column: Int
) {
    companion object {
        fun of(row: Int, column: Int, index: Int): ContainerPosition {
            val position = ContainerPosition(row, column)
            position.index = index
            return position
        }
    }

    var index: Int = 0
        internal set

    var page: Int = 0
        internal set

    operator fun component3() = index
    operator fun component4() = page
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

    fun iterator(rows: Int, columns: Int, rowStart: Int = 0, columnStart: Int = 0) : Iterator<ContainerPosition> {
        return iterator {
            for (index in 0 until (rows * columns)) {
                yield(ContainerPosition.of(
                    anchor.row(rows, direction.row(index, rows, columns)) + rowStart,
                    anchor.column(columns, direction.column(index, rows, columns)) + columnStart,
                    index,
                ))
            }
        }
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
