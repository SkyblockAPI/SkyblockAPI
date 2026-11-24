package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent

public interface AbstractDataVisualizer<Data, Token : AbstractDataVisualizer.VisualizerToken> {

    public val component: MutableComponent
    public var indentCount: Int

    public fun visualize(data: Data): Component {
        visit(data)
        return component
    }

    public fun visit(data: Data)

    public fun VisualizerToken.color(): Int

    public fun append(text: String, color: Int): AbstractDataVisualizer<Data, Token> = apply {
        this.component.append(Text.of(text, color))
    }

    public fun line(): AbstractDataVisualizer<Data, Token> = apply {
        component.append("\n")
    }

    public fun spaces(): AbstractDataVisualizer<Data, Token> = apply {
        component.append("  ".repeat(indentCount))
    }
    public fun appendToken(token: Token): AbstractDataVisualizer<Data, Token> = append(token.token ?: "?", token)
    public fun append(content: String, token: Token): AbstractDataVisualizer<Data, Token> = apply {
        component.append(Text.of(content, token.color()))
    }

    public interface VisualizerToken {
        public val token: String?
    }
}
