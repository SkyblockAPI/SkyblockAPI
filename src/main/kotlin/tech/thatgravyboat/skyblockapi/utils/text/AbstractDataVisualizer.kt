package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

interface AbstractDataVisualizer<Data, Token : AbstractDataVisualizer.VisualizerToken> {

    val component: MutableComponent
    var indentCount: Int

    fun visualize(data: Data): Component {
        visit(data)
        return component
    }

    fun visit(data: Data)

    fun VisualizerToken.color(): Int

    fun append(text: String, color: Int) = apply {
        this.component.append(Text.of(text, color))
    }

    fun line() = apply {
        component.append("\n")
    }

    fun spaces() = apply {
        component.append("  ".repeat(indentCount))
    }
    fun appendToken(token: Token) = append(token.token ?: "?", token)
    fun append(content: String, token: Token) = apply {
        component.append(Text.of(content, token.color()))
    }

    interface VisualizerToken {
        val token: String?
    }
}
