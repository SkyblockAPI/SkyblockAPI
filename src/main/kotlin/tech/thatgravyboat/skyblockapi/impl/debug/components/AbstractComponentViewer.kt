package tech.thatgravyboat.skyblockapi.impl.debug.components

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonNull
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.serialization.DataResult
import me.owdding.ktmodules.Module
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.nbt.Tag
import net.minecraft.nbt.TextComponentTagVisitor
import net.minecraft.network.chat.CommonComponents
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.misc.RegisterCommandsEvent
import tech.thatgravyboat.skyblockapi.api.events.screen.ScreenKeyPressedEvent
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.helpers.McFont
import tech.thatgravyboat.skyblockapi.helpers.McPlayer
import tech.thatgravyboat.skyblockapi.utils.debugToggle
import tech.thatgravyboat.skyblockapi.utils.extentions.getHoveredSlot
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import kotlin.math.sign

val BACKGROUND = Identifier.withDefaultNamespace("popup/background")

class AbstractComponentViewer(val map: Map<ComponentViewerCategory, ComponentViewerData>) : Screen(CommonComponents.EMPTY) {
    val categories = map.keys
    var selectedCategory = map.keys.firstOrNull()
    var scroll = 0

    @Module
    companion object {
        private val toggle by debugToggle("item_component_viewer", "Enables item component viewer Keybind, press \"C\".")

        fun open(item: ComponentViewable) = McClient.setScreenAsync {
            create(item)
        }

        @Subscription
        fun onKey(event: ScreenKeyPressedEvent) {
            if (!toggle) return
            val screen = event.screen as? AbstractContainerScreen<*> ?: return
            if (event.key != InputConstants.KEY_C) return
            open(screen.getHoveredSlot()?.item?.takeUnless { it.isEmpty } ?: return)
            event.cancel()
        }

        @Subscription
        fun onCommandsRegistration(event: RegisterCommandsEvent) {
            event.register("sbapi view") {
                thenCallback("entity") {
                    val hoveredEntity = McClient.self.crosshairPickEntity
                    if (hoveredEntity == null) {
                        Text.debug("No entity is currently hovered.").send()
                    } else {
                        open(hoveredEntity)
                    }
                }
                thenCallback("item") {
                    val heldItem = McPlayer.heldItem.takeUnless { it.isEmpty }
                    if (heldItem == null) {
                        Text.debug("No item is currently held.").send()
                    } else {
                        open(heldItem)
                    }
                }
            }
        }

        fun create(componentViewable: ComponentViewable): AbstractComponentViewer =
            AbstractComponentViewer(componentViewable.`skyblockapi$getComponents`() ?: emptyMap())
    }

    override fun init() {
        if (categories.isNotEmpty()) {
            SelectButton<ComponentViewerCategory>(200, 20).apply {
                for (category in categories) {
                    withEntry(category, Text.of(category.toString()), Text.of(category.toString(), 0xababab), selectedCategory == category)
                }
                singleValue = true
                this.onChange = {
                    selectedCategory = it.first()
                    scroll = 0
                    McClient.runNextTick { runCatching { rebuildWidgets() } }
                }
            }.also(::addRenderableWidget)
        }

        super.init()
    }

    override fun mouseScrolled(x: Double, y: Double, scrollX: Double, scrollY: Double): Boolean {
        this.scroll -= scrollY.sign.toInt()
        this.scroll = scroll.coerceAtLeast(0)
        return super.mouseScrolled(x, y, scrollX, scrollY)
    }

    override fun extractRenderState(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        if (selectedCategory == null || map[selectedCategory] == null) {
            graphics.centeredText(McFont.self, "No entry found!", width / 2, height / 2 - McFont.height / 2, ARGB.opaque(0xFF0000))
        }

        val lines = map[selectedCategory]!!.visualize().splitLines()
        val currentScroll = scroll.coerceIn(0, (lines.size - 5).coerceAtLeast(1))
        this.scroll = currentScroll
        graphics.enableScissor(width / 4, height / 8 + 5, width, height / 8 + widgetHeight - 6)
        lines.drop(currentScroll).take((widgetHeight + 16) / (McFont.height + 1)).forEachIndexed { index, line ->
            graphics.text(McFont.self, line, width / 4 + 8, height / 8 + 6 + (index * (McFont.height + 1)), -1, false)
        }
        graphics.disableScissor()
        super.extractRenderState(graphics, mouseX, mouseY, a)
    }

    val widgetHeight get() = (height / 4) * 3

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, a: Float) {
        super.extractBackground(graphics, mouseX, mouseY, a)
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BACKGROUND, width / 4, height / 8, width / 2, widgetHeight)
    }
}


interface ComponentViewerData {
    fun visualize(): Component
}

data class JsonComponentData(val jsonData: JsonElement) : ComponentViewerData {
    override fun visualize(): Component = JsonVisualizer().serialize(jsonData)
}

class JsonVisualizer {

    val component: MutableComponent = Text.of()
    var indentCount: Int = 0

    fun serialize(data: JsonElement): Component {
        visit(data)
        return component
    }

    fun visit(element: JsonElement) = when (element) {
        is JsonObject -> visitObject(element)
        is JsonArray -> visitArray(element)
        is JsonPrimitive -> visitPrimitive(element)
        is JsonNull -> {
            appendToken(Token.NULL)
        }

        else -> {
            append("<Unknown element type: $element>", -1)
        }
    }

    fun visitPrimitive(element: JsonPrimitive) = when {
        element.isBoolean -> appendToken(if (element.asBoolean) Token.TRUE else Token.FALSE)
        element.isNumber -> append(element.asNumber.toString(), Token.NUMBER)
        else -> appendToken(Token.STRING_QUOTE).append(element.asString, Token.STRING).appendToken(Token.STRING_QUOTE)
    }

    fun visitArray(element: JsonArray) {
        appendToken(Token.OPEN_ARRAY).line()
        indentCount += 1
        val iterator = element.iterator()
        while (iterator.hasNext()) {
            spaces().visit(iterator.next())
            if (iterator.hasNext()) {
                appendToken(Token.COMMA)
            }
            line()
        }
        indentCount -= 1
        spaces().appendToken(Token.CLOSE_ARRAY)
    }

    fun visitObject(element: JsonObject) {
        appendToken(Token.OPEN_OBJECT).line()
        indentCount += 1
        val iterator = element.entrySet().iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            spaces().appendToken(Token.KEY_QUOTE).append(key, Token.KEY).appendToken(Token.KEY_QUOTE)
            appendToken(Token.COLON).appendToken(Token.SPACE)
            visit(value)
            if (iterator.hasNext()) {
                appendToken(Token.COMMA)
            }
            line()
        }
        indentCount -= 1
        spaces().appendToken(Token.CLOSE_OBJECT)
    }

    fun append(text: String, color: Int) = apply {
        this.component.append(Text.of(text, color))
    }

    fun append(text: String, token: Token) = append(text, getColor(token))

    fun line() = apply {
        component.append("\n")
    }

    fun spaces() = apply {
        component.append("  ".repeat(indentCount))
    }

    fun appendToken(token: Token) = apply {
        component.append(Text.of(token.value ?: "?", getColor(token)))
    }

    fun getColor(token: Token): Int = when (token) {
        Token.TRUE -> TextColor.DARK_GREEN
        Token.FALSE -> TextColor.RED
        Token.NULL -> TextColor.GRAY
        Token.KEY, Token.KEY_QUOTE -> TextColor.AQUA
        Token.NUMBER -> TextColor.GOLD
        Token.STRING, Token.STRING_QUOTE -> TextColor.GREEN
        else -> TextColor.WHITE
    }

    enum class Token(val value: String?) {
        OPEN_OBJECT("{"),
        CLOSE_OBJECT("}"),
        OPEN_ARRAY("["),
        CLOSE_ARRAY("]"),
        COLON(":"),
        SPACE(" "),
        COMMA(","),
        KEY_QUOTE("\""),
        STRING_QUOTE("\""),
        TRUE("true"),
        FALSE("false"),
        NULL("null"),
        KEY(null),
        NUMBER(null),
        STRING(null),
    }
}

data class NbtComponentData(val nbtData: Tag) : ComponentViewerData {
    override fun visualize(): Component = TextComponentTagVisitor("  ").visit(nbtData)
}

data class TextComponentData(val text: Component) : ComponentViewerData {
    constructor(components: Collection<Component>) : this(Text.multiline(components))

    override fun visualize(): Component = text
}

data class DataResultComponentData(val data: DataResult<ComponentViewerData>) : ComponentViewerData {
    override fun visualize(): Component {
        if (data.isError) {
            return Text.of {
                append(data.error().get().message())
                this.color = TextColor.RED
            }
        }

        return data.result().get().visualize()
    }
}

interface ComponentViewerCategory {
    override fun toString(): String
}

data object EntityDataCategory : ComponentViewerCategory
data object ItemDataCategory : ComponentViewerCategory
data object BlockDataCategory : ComponentViewerCategory

interface ComponentViewable {
    fun `skyblockapi$getComponents`(): Map<ComponentViewerCategory, ComponentViewerData>?
}

interface ComponentDataAttachable {
    fun `skyblockapi$addComponent`(category: ComponentViewerCategory, entry: ComponentViewerData)
    val `skyblockapi$getComponentMap`: Map<ComponentViewerCategory, ComponentViewerData>?
}

fun ComponentDataAttachable.addComponent(category: ComponentViewerCategory, entry: ComponentViewerData) {
    this.`skyblockapi$addComponent`(category, entry)
}
