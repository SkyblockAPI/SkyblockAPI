package tech.thatgravyboat.skyblockapi.utils.components

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence
import net.minecraft.util.StringDecomposer
import java.util.Optional
import kotlin.jvm.optionals.getOrNull

public enum class StateResult(public val match: Boolean, public val continuation: Boolean) {
    // Mismatch, break
    BREAK(false, false),

    // No match, continue
    CONTINUE(false, true),

    // Match, continue
    CONSUME(true, true),
    ;
}

public interface ComponentStateMachinePart<State> {
    public fun createState(): State

    context(state: State, _: GroupSink)
    public fun tryConsumeState(index: Int, char: Char, style: Style): StateResult

    context(state: State)
    public fun endState(groupSink: GroupSink) {}
}

public interface ComponentStatelessMachinePart : ComponentStateMachinePart<Unit> {
    public override fun createState(): Unit = Unit

    context(_: GroupSink)
    public fun tryConsume(index: Int, char: Char, style: Style): StateResult

    context(state: Unit, _: GroupSink)
    override fun tryConsumeState(index: Int, char: Char, style: Style): StateResult = tryConsume(index, char, style)

    context(state: Unit)
    override fun endState(groupSink: GroupSink): Unit = end(groupSink)

    public fun end(groupSink: GroupSink) {
    }
}

public interface GroupSink {
    public fun group(name: String, group: FormattedCharSequence)
    public fun flush()
}

public fun interface CharSink {
    public fun tryConsume(char: Char): Boolean
}

public data class LiteralComponentPart(val literal: String) : ComponentStatelessMachinePart {
    context(_: GroupSink)
    override fun tryConsume(index: Int, char: Char, style: Style): StateResult {
        if (index < literal.length) {
            return if (literal[index] == char) {
                StateResult.CONSUME
            } else {
                StateResult.BREAK
            }
        }

        return StateResult.CONTINUE
    }
}

public data class WildcardComponentPart(val sink: CharSink, val maxLength: Int = 10000) : ComponentStatelessMachinePart {
    context(_: GroupSink)
    override fun tryConsume(index: Int, char: Char, style: Style): StateResult {
        if (index <= maxLength && sink.tryConsume(char)) {
            return StateResult.CONSUME
        }

        return StateResult.CONTINUE
    }
}

public data class CompositeComponentPart(
    val children: List<ComponentStateMachinePart<*>>,
) : ComponentStateMachinePart<CompositeComponentPart.State> {
    public data class State(
        var current: StateMachinePosition<*>,
        val part: Iterator<ComponentStateMachinePart<*>>,
    )

    override fun createState(): State {
        val iterator = children.iterator()

        return State(StateMachinePosition(iterator.next()), iterator)
    }

    context(state: State, _: GroupSink)
    override fun tryConsumeState(index: Int, char: Char, style: Style): StateResult {
        val result = state.current.tryConsume(char, style)
        if (result.match) {
            return result
        }

        state.current.end()

        if (!result.continuation) {
            return StateResult.BREAK
        }

        while (state.part.hasNext()) {
            state.current = StateMachinePosition(state.part.next())
            val result = state.current.tryConsume(char, style)
            if (result.match || !result.continuation) {
                return result
            }
        }

        return StateResult.CONTINUE
    }

    context(state: State)
    override fun endState(groupSink: GroupSink) {
        state.current.endState(groupSink)
    }
}

public data class RepeatPart(
    val children: ComponentStateMachinePart<*>,
    val delimiter: ComponentStateMachinePart<*>,
) : ComponentStateMachinePart<RepeatPart.State> {
    override fun createState(): State = State(StateMachinePosition(children))

    public data class State(
        var current: StateMachinePosition<*>,
        var isDelimiter: Boolean = false,
    )

    context(state: State, @Suppress("PARAMETER_NAME_CHANGED_ON_OVERRIDE") sink: GroupSink)
    override fun tryConsumeState(index: Int, char: Char, style: Style): StateResult {
        val result = state.current.tryConsume(char, style)
        if (result.match || !result.continuation) {
            return result
        }

        if (state.isDelimiter) {
            sink.flush()
            state.isDelimiter = false
            state.current.end()
            state.current = StateMachinePosition(children)
            val result = state.current.tryConsume(char, style)
            if (result.match || !result.continuation) {
                return result
            }

        } else {
            state.isDelimiter = true
            state.current.end()
            state.current = StateMachinePosition(delimiter)
            val result = state.current.tryConsume(char, style)
            if (result.match || !result.continuation) {
                return result
            }

        }

        state.current.end()
        return StateResult.BREAK
    }

    context(state: State)
    override fun endState(groupSink: GroupSink) {
        state.current.endState(groupSink)
    }
}

public data class ForkPart(
    val forks: List<ComponentStateMachinePart<*>>,
    val marker: Unit = Unit,
) : ComponentStateMachinePart<ForkPart.State> {
    @Deprecated("Only for binary compat", level = DeprecationLevel.HIDDEN)
    public constructor(forks: List<StateMachinePosition<*>>) : this(forks.map { it.part }, Unit)

    public data class State(
        var state: StateMachinePosition<*>? = null,
    )

    override fun createState(): State = State()

    context(state: State, _: GroupSink)
    override fun tryConsumeState(index: Int, char: Char, style: Style): StateResult {
        if (index == 0) {
            val machine = forks.map { StateMachinePosition(it) }.find {
                it.tryConsume(char, style).match
            } ?: return StateResult.BREAK
            state.state = machine
            return StateResult.CONSUME
        }

        return state.state?.tryConsume(char, style) ?: StateResult.BREAK
    }

    context(state: State)
    override fun endState(groupSink: GroupSink) {
        state.state?.endState(groupSink)
    }
}

public data class OptionalPart(
    //? <= 26.3
    @get:JvmName("newPart")
    val part: ComponentStateMachinePart<*>,
) : ComponentStateMachinePart<StateMachinePosition<*>> {
    public constructor(part: StateMachinePosition<*>) : this(part.part)

    //? <= 26.3
    @get:JvmName("part") val _part: StateMachinePosition<out Any?> get() = StateMachinePosition(part)

    override fun createState(): StateMachinePosition<*> = StateMachinePosition(part)

    context(state: StateMachinePosition<*>, _: GroupSink)
    override fun tryConsumeState(
        index: Int,
        char: Char,
        style: Style,
    ): StateResult {
        val result = state.tryConsume(char, style)
        if (index == 0 && !result.continuation) {
            return StateResult.CONTINUE
        }
        return result
    }

    context(state: StateMachinePosition<*>)
    override fun endState(groupSink: GroupSink) {
        state.endState(groupSink)
    }

}

public data class CapturingComponentPart(
    val children: ComponentStateMachinePart<*>,
    val name: String,
) : ComponentStateMachinePart<CapturingComponentPart.State> {
    public data class State(
        var current: StateMachinePosition<*>,
        val result: MutableList<Pair<Char, Style>> = ArrayList(),
    )

    override fun createState(): State {
        return State(StateMachinePosition(children))
    }

    context(state: State)
    public fun capture(char: Char, style: Style) {
        state.result.add(Pair(char, style))
    }

    context(state: State, _: GroupSink)
    override fun tryConsumeState(index: Int, char: Char, style: Style): StateResult {
        val result = state.current.tryConsume(char, style)
        if (result.match) {
            capture(char, style)
            return result
        }

        return result
    }

    context(state: State)
    override fun endState(groupSink: GroupSink) {
        context(groupSink) {
            state.current.end()
        }
        val result = state.result
        groupSink.group(name) {
            result.forEachIndexed { index, (char, style) ->
                if (!it.accept(index, style, char.code)) {
                    return@group false
                }
            }

            true
        }
    }
}

public class ForkBuilder() : StateMachineBuilder() {

    public fun branch(builder: StateMachineBuilder.() -> Unit): Unit = add {
        StateMachineBuilder().apply(builder).toPart()
    }

    override fun toPart(): CompositeComponentPart {
        return CompositeComponentPart(listOf(ForkPart(parts)))
    }
}

public open class StateMachineBuilder(
    public val parts: MutableList<ComponentStateMachinePart<*>> = mutableListOf(),
) {

    public fun repeat(delimiter: String, builder: StateMachineBuilder.() -> Unit): Unit = add {
        RepeatPart(
            StateMachineBuilder().apply(builder).toPart(),
            LiteralComponentPart(delimiter),
        )
    }

    public fun literal(string: String): Unit = add {
        LiteralComponentPart(string)
    }

    public fun wildcard(char: CharSink, maxLength: Int = 10000): Unit = add {
        WildcardComponentPart(char, maxLength)
    }

    public fun char(char: Char): Unit = literal(char.toString())

    public fun chars(vararg char: Iterable<Char>, maxLength: Int = 10000): Unit = add {
        val chars = char.flatMapTo(mutableSetOf()) { it.toList() }
        WildcardComponentPart({ it in chars }, maxLength)
    }

    public fun capture(name: String, builder: StateMachineBuilder.() -> Unit): Unit = add {
        CapturingComponentPart(StateMachineBuilder().apply(builder).toPart(), name)
    }

    public fun optional(builder: StateMachineBuilder.() -> Unit): Unit = add {
        OptionalPart(StateMachineBuilder().apply(builder).toPart())
    }

    public fun fork(builder: ForkBuilder.() -> Unit): Unit = add {
        ForkBuilder().apply(builder).toPart()
    }

    public inline fun add(supplier: () -> ComponentStateMachinePart<*>) {
        parts.add(supplier.invoke())
    }

    public open fun toPart(): CompositeComponentPart = CompositeComponentPart(parts)
}

public class StateMachinePosition<Type>(
    public val part: ComponentStateMachinePart<Type>,
    public val state: Type = part.createState(),
    public var index: Int = 0,
) {
    context(groupSink: GroupSink)
    public fun tryConsume(char: Char, style: Style): StateResult = context(state) {
        part.tryConsumeState(index++, char, style)
    }

    public fun endState(groupSink: GroupSink): Unit = context(groupSink) {
        end()
    }

    context(groupSink: GroupSink)
    public fun end(): Unit = context(state) {
        part.endState(groupSink)
    }
}

public class ComponentStateMachine(
    public val parts: ComponentStateMachinePart<*>,
) {
    public companion object {
        public fun build(builder: StateMachineBuilder.() -> Unit): ComponentStateMachine = ComponentStateMachine(StateMachineBuilder().apply(builder).toPart())
    }

    public constructor(parts: List<ComponentStateMachinePart<*>>) : this(CompositeComponentPart(parts))

    public fun decompose(component: Component, consumer: (Map<String, FormattedCharSequence>) -> Unit): Boolean {
        val current = StateMachinePosition(parts)

        val groups = mutableMapOf<String, FormattedCharSequence>()
        val sink = object : GroupSink {
            override fun flush() {
                consumer(groups)
                groups.clear()
            }

            override fun group(name: String, group: FormattedCharSequence) {
                groups[name] = group
            }

        }

        context(sink) {
            val success = StringDecomposer.iterateFormatted(component, Style.EMPTY) { position, style, codepoint ->
                for (ch in Character.toString(codepoint)) {
                    val result = current.tryConsume(ch, style)
                    if (result.continuation && result.match) {
                        continue
                    }
                    return@iterateFormatted false
                }

                true
            }

            current.end()
            sink.flush()

            return success
        }
    }

    public fun match(component: Component, consumer: (Map<String, FormattedCharSequence>) -> Unit): Boolean {
        val groups = mutableMapOf<String, FormattedCharSequence>()

        val current = StateMachinePosition(parts)
        val sink = object : GroupSink {
            override fun flush() {
                consumer(groups)
                groups.clear()
            }

            override fun group(name: String, group: FormattedCharSequence) {
                groups[name] = group
            }

        }
        context(sink) {
            val isSuccess = component.visit(
                { style, contents ->
                    for (char in contents) {
                        val result = current.tryConsume(char, style)
                        if (result.continuation && result.match) {
                            continue
                        }
                        return@visit Optional.of(false)
                    }
                    return@visit Optional.empty()
                },
                Style.EMPTY,
            )
            current.end()
            sink.flush()

            return isSuccess.getOrNull() != false
        }
    }

}
