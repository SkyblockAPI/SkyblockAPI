package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import net.minecraft.network.chat.contents.objects.AtlasSprite
import net.minecraft.network.chat.contents.objects.PlayerSprite
import net.minecraft.resources.Identifier
import net.minecraft.world.item.component.ResolvableProfile
import tech.thatgravyboat.skyblockapi.helpers.McClient
import tech.thatgravyboat.skyblockapi.impl.events.chat.setMessageId
import tech.thatgravyboat.skyblockapi.utils.text.TextBuilder.append
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

public object Text {

    public inline fun of(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = text.asComponent(init)
    public inline fun of(init: MutableComponent.() -> Unit = {}): MutableComponent = Component.empty().also(init)
    public fun of(text: String, color: Int): MutableComponent = of(text) { this.color = color }
    public inline fun translatable(text: String, init: MutableComponent.() -> Unit = {}): MutableComponent = Component.translatable(text).also(init)

    public fun player(profile: ResolvableProfile, hat: Boolean = true, init: MutableComponent.() -> Unit = {}): MutableComponent {
        val spriteObj = PlayerSprite(profile, hat)
        return Component.`object`(spriteObj).also(init)
    }

    public fun atlas(atlas: Identifier, sprite: Identifier, init: MutableComponent.() -> Unit = {}): MutableComponent {
        val spriteObj = AtlasSprite(atlas, sprite)
        return Component.`object`(spriteObj).also(init)
    }

    public inline fun String.asComponent(init: MutableComponent.() -> Unit = {}): MutableComponent = Component.literal(this).also(init)

    @JvmOverloads
    public fun multiline(vararg lines: Any?, init: MutableComponent.() -> Unit = {}): MutableComponent = join(*lines, separator = CommonText.NEWLINE, init = init)

    @JvmOverloads
    public fun join(vararg components: Any?, separator: Component? = null, init: MutableComponent.() -> Unit = {}): MutableComponent {
        val result = Component.literal("")
        components.forEachIndexed { index, it ->
            when (it) {
                is Component -> result.append(it)
                is String -> result.append(it)
                is Char -> result.append(it.toString())
                is Collection<*> -> result.append(join(*it.toTypedArray(), separator = separator))
                is ComponentLike -> result.append(it)
                is Number -> result.append(it)
                is Boolean -> result.append(it)
                null -> return@forEachIndexed
                else -> error("Unsupported type: ${it::class.simpleName}")
            }

            if (index < components.size - 1 && separator != null) {
                result.append(separator)
            }
        }
        return result.also(init)
    }

    /** 
     * Returns a component containing this component repeated n times.
     *
     * @param n Amount of repetitions
     */
    public fun Component.repeat(n: Int): MutableComponent = join(List(n) { this })
    public fun Component.prefix(prefix: String): MutableComponent = join(prefix, this)
    public fun Component.suffix(suffix: String): MutableComponent = join(this, suffix)
    public fun Component.wrap(prefix: String, suffix: String): MutableComponent = this.prefix(prefix).suffix(suffix)
    public inline fun Component.wrap(prefix: String, suffix: String, init: MutableComponent.() -> Unit): MutableComponent = this.prefix(prefix).suffix(suffix).apply(init)

    public inline fun Component.copy(block: MutableComponent.() -> Unit = {}): MutableComponent = copy().apply(block)

    public fun Component.send() {
        McClient.chat.addClientSystemMessage(this)
    }

    public fun Component.send(id: String): Unit = McClient.chat.setMessageId(id) {
        this.send()
    }

    internal fun debug(text: String = "", init: MutableComponent.() -> Unit = {}) =
        of("[SkyBlockAPI] $text") {
            this.color = TextColor.YELLOW
            init.invoke(this)
        }

    internal fun sendDebug(text: String = "", init: MutableComponent.() -> Unit = {}) = debug(text, init).send()
    internal fun Component.sendWithPrefix() = join(CommonText.PREFIX, CommonText.SPACE, this).send()
    internal fun Component.sendWithPrefix(id: String) = join(CommonText.PREFIX, CommonText.SPACE, this).send(id)
}

