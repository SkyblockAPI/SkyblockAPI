package tech.thatgravyboat.skyblockapi.utils.builders

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.text.CommonText
import tech.thatgravyboat.skyblockapi.utils.text.Text

public class TooltipBuilder() {
    public constructor(lines: List<Component>) : this() {
        this.lines.addAll(lines)
    }

    public companion object {
        public fun multiline(init: TooltipBuilder.() -> Unit): MutableComponent = Text.multiline(TooltipBuilder().also(init).lines)
    }

    private val lines = mutableListOf<Component>()

    public fun add(line: Component): Boolean = lines.add(line)

    public fun space(): Boolean = lines.add(CommonText.EMPTY)

    public fun add(number: Number, init: MutableComponent.() -> Unit = {}): Boolean = lines.add(Text.of(number.toString(), init))
    public fun add(boolean: Boolean, init: MutableComponent.() -> Unit = {}): Boolean = lines.add(Text.of(boolean.toString(), init))
    public fun add(text: String, init: MutableComponent.() -> Unit = {}): Boolean = lines.add(Text.of(text, init))
    public fun add(text: String, color: Int): Boolean = lines.add(Text.of(text).withColor(color))
    public fun add(init: MutableComponent.() -> Unit): Boolean = lines.add(Text.of("", init))

    public fun isEmpty(): Boolean = lines.isEmpty()
    public fun build(): Component = Text.multiline(*lines.toTypedArray())
    public fun lines(): MutableList<Component> = lines
}
