package tech.thatgravyboat.skyblockapi.impl.debug

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.msrandom.stub.Stub
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import kotlin.reflect.KClass
import kotlin.time.Instant

@Stub
internal expect fun DebugScreen(
    title: String,
    messages: List<Pair<Instant, Any>>,
    buttons: List<AbstractWidget> = emptyList(),
    asSearch: (Any) -> String,
    display: (Any) -> Component = { Text.of(it.toString()) },
    tooltip: (Any) -> Component = { Text.of("Click to copy to clipboard") { this.color = TextColor.GRAY; } },
    onClicked: (Any) -> Unit,
    timeFormat: String = "HH:mm:ss",
    type: KClass<Any>,
): Screen

internal inline fun <reified T> DebugScreen(
    title: String,
    messages: List<Pair<Instant, T>>,
    buttons: List<AbstractWidget> = emptyList(),
    noinline asSearch: (T) -> String,
    noinline display: (T) -> Component = { Text.of(it.toString()) },
    noinline tooltip: (T) -> Component = { Text.of("Click to copy to clipboard") { this.color = TextColor.GRAY; } },
    noinline onClicked: (T) -> Unit,
    timeFormat: String = "HH:mm:ss",
): Screen = DebugScreen(
    title,
    messages.unsafeCast(),
    buttons,
    asSearch.unsafeCast(),
    display.unsafeCast(),
    tooltip.unsafeCast(),
    onClicked.unsafeCast(),
    timeFormat,
    T::class.unsafeCast(),
)

@Suppress("UNCHECKED_CAST")
private fun <T, R> T.unsafeCast() = this as R
