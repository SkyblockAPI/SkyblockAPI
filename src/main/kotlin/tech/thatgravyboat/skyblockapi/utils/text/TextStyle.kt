package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.FontDescription
import net.minecraft.network.chat.HoverEvent
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.Style
import net.minecraft.resources.Identifier
import tech.thatgravyboat.skyblockapi.hooks.RunnableClickEventHook
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.style
import java.net.URI


private fun MutableComponent.withFont(location: Identifier?): MutableComponent =
    this.style { if (location == null) withFont(null) else withFont(FontDescription.Resource(location)) }

private fun Component.font(): Identifier? = (this.style.font as? FontDescription.Resource)?.id

private fun Component.hover(): Component? = (this.style.hoverEvent as? HoverEvent.ShowText)?.value()

private fun Component.command(): String? = (this.style.clickEvent as? ClickEvent.RunCommand)?.command()

private fun Component.customPayloadClick(): ClickEvent.Custom? = this.style.clickEvent as? ClickEvent.Custom

private fun Component.clipboard(): String? = (this.style.clickEvent as? ClickEvent.CopyToClipboard)?.value()

private fun Component.suggest(): String? = (this.style.clickEvent as? ClickEvent.SuggestCommand)?.command()

private fun Component.uri(): URI? = (this.style.clickEvent as? ClickEvent.OpenUrl)?.uri()

private fun Component.url(): String? = this.uri()?.toString()

private fun Component.color(): Int = this.style.color?.value ?: 0

private fun Component.shadowColor(): Int? = this.style.shadowColor

private fun Component.bold(): Boolean = this.style.isBold

private fun Component.italic(): Boolean = this.style.isItalic

private fun Component.underlined(): Boolean = this.style.isUnderlined

private fun Component.strikethrough(): Boolean = this.style.isStrikethrough

private fun Component.obfuscated(): Boolean = this.style.isObfuscated

object TextStyle {

    fun MutableComponent.style(init: Style.() -> Style): MutableComponent {
        this.withStyle { init.invoke(style) }
        return this
    }

    fun MutableComponent.onClick(runnable: () -> Unit): MutableComponent = this.style {
        val event = ClickEvent.SuggestCommand("SkyBlockAPI OnClick Action")
        @Suppress("KotlinConstantConditions")
        ((event as Any) as? RunnableClickEventHook)?.`skyblockapi$setRunnable` { runnable() }
        withClickEvent(event)
    }


    val Component.font: Identifier?
        get() = font()

    var MutableComponent.font: Identifier?
        get() = font()
        set(value) {
            this.withFont(value)
        }


    val Component.hover: Component?
        get() = hover()

    var MutableComponent.hover: Component?
        get() = hover()
        set(value) {
            this.style { withHoverEvent(value?.let { HoverEvent.ShowText(it) }) }
        }


    val Component.clipboard: String?
        get() = clipboard()

    var MutableComponent.clipboard: String?
        get() = clipboard()
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent.CopyToClipboard(it) }) }
        }


    val Component.command: String?
        get() = command()

    var MutableComponent.command: String?
        get() = command()
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent.RunCommand(it) }) }
        }


    val Component.suggest: String?
        get() = suggest()

    var MutableComponent.suggest: String?
        get() = suggest()
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent.SuggestCommand(it) }) }
        }


    val Component.customPayloadClick: ClickEvent.Custom?
        get() = customPayloadClick()

    var MutableComponent.customPayloadClick: ClickEvent.Custom?
        get() = customPayloadClick()
        set(value) {
            this.style { withClickEvent(value) }
        }


    val Component.uri: URI?
        get() = uri()

    var MutableComponent.uri: URI?
        get() = uri()
        set(value) {
            this.style { withClickEvent(value?.let { ClickEvent.OpenUrl(it) }) }
        }


    val Component.url: String?
        get() = url()

    var MutableComponent.url: String?
        get() = url()
        set(value) {
            this.uri = value?.let(URI::create)
        }


    val Component.color: Int
        get() = color()

    var MutableComponent.color: Int
        get() = color()
        set(value) {
            this.style { withColor(value) }
        }


    val Component.shadowColor: Int?
        get() = shadowColor()

    var MutableComponent.shadowColor: Int?
        get() = shadowColor()
        set(value) {
            this.style { this.withShadowColor(value ?: 0) }
        }


    val Component.bold: Boolean
        get() = bold()

    var MutableComponent.bold: Boolean
        get() = bold()
        set(value) {
            this.style { withBold(value) }
        }


    val Component.italic: Boolean
        get() = italic()

    var MutableComponent.italic: Boolean
        get() = italic()
        set(value) {
            this.style { withItalic(value) }
        }


    val Component.underlined: Boolean
        get() = underlined()

    var MutableComponent.underlined: Boolean
        get() = underlined()
        set(value) {
            this.style { withUnderlined(value) }
        }


    val Component.strikethrough: Boolean
        get() = strikethrough()

    var MutableComponent.strikethrough: Boolean
        get() = strikethrough()
        set(value) {
            this.style { withStrikethrough(value) }
        }


    val Component.obfuscated: Boolean
        get() = obfuscated()

    var MutableComponent.obfuscated: Boolean
        get() = obfuscated()
        set(value) {
            this.style { withObfuscated(value) }
        }
}
