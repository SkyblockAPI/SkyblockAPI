package tech.thatgravyboat.skyblockapi.impl.debug

import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.msrandom.stub.Stub
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color
import kotlin.time.Instant

@Stub
internal expect fun <T> DebugScreen(
    title: String,
    messages: List<Pair<Instant, T>>,
    buttons: List<AbstractWidget> = emptyList(),
    asSearch: (T) -> String,
    display: (T) -> Component = { Text.of(it.toString()) },
    tooltip: (T) -> Component = { Text.of("Click to copy to clipboard") { this.color = TextColor.GRAY; } },
    onClicked: (T) -> Unit,
    timeFormat: String = "HH:mm:ss",
): Screen
