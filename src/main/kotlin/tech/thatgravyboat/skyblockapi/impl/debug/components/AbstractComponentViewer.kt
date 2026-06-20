package tech.thatgravyboat.skyblockapi.impl.debug.components

import com.google.gson.JsonElement
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
import tech.thatgravyboat.skyblockapi.utils.text.JsonVisualizer
import tech.thatgravyboat.skyblockapi.utils.text.NbtVisualizer
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.Text.send
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import tech.thatgravyboat.skyblockapi.utils.text.TextUtils.splitLines
import tech.thatgravyboat.skyblockapi.utils.text.asComponent
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
    override fun visualize(): Component = jsonData.asComponent()
}

data class NbtComponentData(val nbtData: Tag) : ComponentViewerData {
    override fun visualize(): Component = nbtData.asComponent()
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
