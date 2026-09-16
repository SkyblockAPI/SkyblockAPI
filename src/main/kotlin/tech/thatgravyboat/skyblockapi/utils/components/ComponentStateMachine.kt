package tech.thatgravyboat.skyblockapi.utils.components

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.FormattedCharSequence
import java.util.*
import kotlin.jvm.optionals.getOrNull

enum class StateResult(val match: Boolean, val continuation: Boolean) {
    // Mismatch, break
    BREAK(false, false),

    // No match, continue
    CONTINUE(false, true),

    // Match, continue
    CONSUME(true, true),
    ;
}

interface ComponentStateMachinePart<State> {
    fun createState(): State

    context(state: State, _: GroupSink)
    fun tryConsumeState(index: Int, char: Char, style: Style): StateResult

    context(state: State)
    fun endState(groupSink: GroupSink) {
    }
}

interface ComponentStatelessMachinePart : ComponentStateMachinePart<Unit> {
    override fun createState() = Unit

    context(_: GroupSink)
    fun tryConsume(index: Int, char: Char, style: Style): StateResult

    context(state: Unit, _: GroupSink)
    override fun tryConsumeState(index: Int, char: Char, style: Style): StateResult = tryConsume(index, char, style)

    context(state: Unit)
    override fun endState(groupSink: GroupSink) = end(groupSink)

    fun end(groupSink: GroupSink) {
    }
}

interface GroupSink {
    fun group(name: String, group: FormattedCharSequence)
    fun flush()
}

fun interface CharSink {
    fun tryConsume(char: Char): Boolean
}

data class LiteralComponentPart(val literal: String) : ComponentStatelessMachinePart {
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

data class WildcardComponentPart(val sink: CharSink, val maxLength: Int = 10000) : ComponentStatelessMachinePart {
    context(_: GroupSink)
    override fun tryConsume(index: Int, char: Char, style: Style): StateResult {
        if (index <= maxLength && sink.tryConsume(char)) {
            return StateResult.CONSUME
        }

        return StateResult.CONTINUE
    }
}

data class CompositeComponentPart(
    val children: List<ComponentStateMachinePart<*>>,
) : ComponentStateMachinePart<CompositeComponentPart.State> {
    data class State(
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

data class RepeatPart(
    val children: ComponentStateMachinePart<*>,
    val delimiter: ComponentStateMachinePart<*>,
) : ComponentStateMachinePart<RepeatPart.State> {
    override fun createState(): State = State(StateMachinePosition(children))

    data class State(
        var current: StateMachinePosition<*>,
        var isDelimiter: Boolean = false,
    )

    context(state: State, sink: GroupSink)
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

data class ForkPart(
    val forks: List<StateMachinePosition<*>>,
) : ComponentStateMachinePart<ForkPart.State> {
    data class State(
        var state: StateMachinePosition<*>? = null,
    )

    override fun createState(): State = State()

    context(state: State, _: GroupSink)
    override fun tryConsumeState(index: Int, char: Char, style: Style): StateResult {
        if (index == 0) {
            val machine = forks.find {
                it.tryConsume(char, style).match
            } ?: return StateResult.BREAK
            state.state = machine
            return StateResult.CONSUME
        }
        return state.state!!.tryConsume(char, style)
    }

    context(state: State)
    override fun endState(groupSink: GroupSink) {
        state.state?.endState(groupSink)
    }
}

data class OptionalPart(
    val part: StateMachinePosition<*>,
) : ComponentStatelessMachinePart {
    constructor(part: ComponentStateMachinePart<*>) : this(StateMachinePosition(part))

    context(_: GroupSink)
    override fun tryConsume(
        index: Int,
        char: Char,
        style: Style,
    ): StateResult {
        val result = part.tryConsume(char, style)
        if (index == 0 && !result.continuation) {
            return StateResult.CONTINUE
        }
        return result
    }

    override fun end(groupSink: GroupSink) {
        part.endState(groupSink)
    }
}

data class CapturingComponentPart(
    val children: ComponentStateMachinePart<*>,
    val name: String,
) : ComponentStateMachinePart<CapturingComponentPart.State> {
    data class State(
        var current: StateMachinePosition<*>,
        val result: MutableList<Pair<Char, Style>> = ArrayList(),
    )

    override fun createState(): State {
        return State(StateMachinePosition(children))
    }

    context(state: State)
    fun capture(char: Char, style: Style) {
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

class ForkBuilder() : StateMachineBuilder() {

    fun branch(builder: StateMachineBuilder.() -> Unit) = add {
        StateMachineBuilder().apply(builder).toPart()
    }

}

open class StateMachineBuilder(
    val parts: MutableList<ComponentStateMachinePart<*>> = mutableListOf(),
) {

    fun repeat(delimiter: String, builder: StateMachineBuilder.() -> Unit) = add {
        RepeatPart(
            StateMachineBuilder().apply(builder).toPart(),
            LiteralComponentPart(delimiter),
        )
    }

    fun literal(string: String) = add {
        LiteralComponentPart(string)
    }

    fun wildcard(char: CharSink, maxLength: Int = 10000) = add {
        WildcardComponentPart(char, maxLength)
    }

    fun char(char: Char) = literal(char.toString())

    fun chars(vararg char: Iterable<Char>, maxLength: Int = 10000) = add {
        val chars = char.flatMapTo(mutableSetOf()) { it.toList() }
        WildcardComponentPart({ it in chars }, maxLength)
    }

    fun capture(name: String, builder: StateMachineBuilder.() -> Unit) = add {
        CapturingComponentPart(StateMachineBuilder().apply(builder).toPart(), name)
    }

    fun optional(builder: StateMachineBuilder.() -> Unit) = add {
        OptionalPart(StateMachineBuilder().apply(builder).toPart())
    }

    fun fork(builder: ForkBuilder.() -> Unit) = add {
        OptionalPart(ForkBuilder().apply(builder).toPart())
    }

    inline fun add(supplier: () -> ComponentStateMachinePart<*>) {
        parts.add(supplier.invoke())
    }

    fun toPart() = CompositeComponentPart(parts)
}

class StateMachinePosition<Type>(
    val part: ComponentStateMachinePart<Type>,
    val state: Type = part.createState(),
    var index: Int = 0,
) {
    context(groupSink: GroupSink)
    fun tryConsume(char: Char, style: Style): StateResult = context(state) {
        part.tryConsumeState(index++, char, style)
    }

    fun endState(groupSink: GroupSink) = context(groupSink) {
        end()
    }

    context(groupSink: GroupSink)
    fun end() = context(state) {
        part.endState(groupSink)
    }
}

class ComponentStateMachine(
    val parts: ComponentStateMachinePart<*>,
) {
    companion object {
        fun build(builder: StateMachineBuilder.() -> Unit): ComponentStateMachine = ComponentStateMachine(StateMachineBuilder().apply(builder).toPart())
    }

    constructor(parts: List<ComponentStateMachinePart<*>>) : this(CompositeComponentPart(parts))

    fun match(component: Component, consumer: (Map<String, FormattedCharSequence>) -> Unit): Boolean {
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
