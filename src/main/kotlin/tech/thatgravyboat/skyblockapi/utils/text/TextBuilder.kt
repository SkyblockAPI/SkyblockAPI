package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text.asComponent
import tech.thatgravyboat.skyblockapi.utils.text.Text.copy
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

object TextBuilder {
    fun MutableComponent.append(like: ComponentLike): MutableComponent = this.append(like.toComponent())
    fun MutableComponent.append(init: MutableComponent.() -> Unit): MutableComponent = this.append(Text.of(init))
    fun MutableComponent.append(component: Component, init: MutableComponent.() -> Unit): MutableComponent = this.append(component.copy(init))
    fun MutableComponent.append(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(text.asComponent(init))
    fun MutableComponent.append(number: Number, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(number.toString().asComponent(init))
    fun MutableComponent.append(boolean: Boolean, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(boolean.toString().asComponent(init))
    fun MutableComponent.append(text: String, color: Int): MutableComponent = this.append(text) { this.color = color }

    /** All of these do the same thing as their counterparts above, except they add a newline after them */
    fun MutableComponent.appendLine(): MutableComponent = this.append(CommonText.NEWLINE)
    fun MutableComponent.appendLine(string: String): MutableComponent = append(string).appendLine()
    fun MutableComponent.appendLine(like: ComponentLike): MutableComponent = this.append(like).appendLine()
    fun MutableComponent.appendLine(init: MutableComponent.() -> Unit): MutableComponent = this.append(init).appendLine()
    fun MutableComponent.appendLine(component: Component, init: MutableComponent.() -> Unit): MutableComponent = this.append(component, init).appendLine()
    fun MutableComponent.appendLine(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(text, init).appendLine()
    fun MutableComponent.appendLine(number: Number, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(number, init).appendLine()
    fun MutableComponent.appendLine(boolean: Boolean, init: MutableComponent.() -> Unit = {}): MutableComponent = this.append(boolean, init).appendLine()
    fun MutableComponent.appendLine(text: String, color: Int): MutableComponent = this.append(text, color).appendLine()

}
