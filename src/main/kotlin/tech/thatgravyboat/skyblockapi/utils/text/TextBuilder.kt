package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text.copy
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

public object TextBuilder {
    public fun MutableComponent.append(like: ComponentLike): MutableComponent = this.append(like.toComponent())
    public fun MutableComponent.append(init: MutableComponent.() -> Unit): MutableComponent = this.append(Text.of(init))
    public fun MutableComponent.append(component: Component, init: MutableComponent.() -> Unit): MutableComponent = this.append(component.copy(init))
    public fun MutableComponent.append(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(text.asComponent(init))
    public fun MutableComponent.append(number: Number, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(number.toString().asComponent(init))
    public fun MutableComponent.append(boolean: Boolean, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(boolean.toString().asComponent(init))
    public fun MutableComponent.append(text: String, color: Int): MutableComponent = this.append(text) { this.color = color }

    /** All of these do the same thing as their counterparts above, except they add a newline after them */
    public fun MutableComponent.appendLine(): MutableComponent = this.append(CommonText.NEWLINE)
    public fun MutableComponent.appendLine(string: String): MutableComponent = append(string).appendLine()
    public fun MutableComponent.appendLine(like: ComponentLike): MutableComponent = this.append(like).appendLine()
    public fun MutableComponent.appendLine(init: MutableComponent.() -> Unit): MutableComponent = this.append(init).appendLine()
    public fun MutableComponent.appendLine(component: Component, init: MutableComponent.() -> Unit): MutableComponent = this.append(component, init).appendLine()
    public fun MutableComponent.appendLine(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(text, init).appendLine()
    public fun MutableComponent.appendLine(number: Number, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(number, init).appendLine()
    public fun MutableComponent.appendLine(boolean: Boolean, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(boolean, init).appendLine()
    public fun MutableComponent.appendLine(text: String, color: Int): MutableComponent = this.append(text, color).appendLine()

}
