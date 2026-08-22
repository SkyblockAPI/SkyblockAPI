package tech.thatgravyboat.skyblockapi.utils.text

import net.minecraft.nbt.ByteArrayTag
import net.minecraft.nbt.ByteTag
import net.minecraft.nbt.CollectionTag
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.DoubleTag
import net.minecraft.nbt.EndTag
import net.minecraft.nbt.FloatTag
import net.minecraft.nbt.IntArrayTag
import net.minecraft.nbt.IntTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.LongArrayTag
import net.minecraft.nbt.LongTag
import net.minecraft.nbt.ShortTag
import net.minecraft.nbt.StringTag
import net.minecraft.nbt.Tag
import net.minecraft.network.chat.MutableComponent

fun Tag.asComponent() = NbtVisualizer().visualize(this)

class NbtVisualizer : AbstractDataVisualizer<Tag, NbtVisualizer.Token> {

    var ignoreSuffix: Boolean = false
    override val component: MutableComponent = Text.of()
    override var indentCount: Int = 0
    override fun visit(data: Tag): Unit = when (data) {
        is ByteArrayTag -> visitArray(data, Token.BYTE_ARRAY_PREFIX)
        is IntArrayTag -> visitArray(data, Token.INT_ARRAY_PREFIX)
        is LongArrayTag -> visitArray(data, Token.LONG_ARRAY_PREFIX)
        is ListTag -> visitArray(data, null)
        is CompoundTag -> visitCompound(data)
        is EndTag -> {}
        is ByteTag -> visitByte(data)
        is DoubleTag -> visitDouble(data)
        is FloatTag -> visitFloat(data)
        is IntTag -> visitInt(data)
        is LongTag -> visitLong(data)
        is ShortTag -> visitShort(data)
        is StringTag -> visitString(data)
    }


    fun visitArray(collectionTag: CollectionTag, type: Token?) {
        appendToken(Token.LIST_OPEN)
        if (type != null) {
            appendToken(type).appendToken(Token.LIST_TYPE_SEPARATOR)
        }
        line()
        indentCount += 1
        val previous = ignoreSuffix
        ignoreSuffix = false || type != null
        val iterator = collectionTag.iterator()
        while (iterator.hasNext()) {
            spaces().visit(iterator.next())
            if (iterator.hasNext()) {
                appendToken(Token.ELEMENT_SEPARATOR)
            }
            line()
        }
        ignoreSuffix = previous
        indentCount -= 1
        spaces().appendToken(Token.LIST_CLOSE)
    }

    fun visitByte(byteTag: ByteTag) {
        append(byteTag.value.toString(), Token.NUMBER)
        if (!ignoreSuffix) appendToken(Token.BYTE_SUFFIX)
    }

    fun visitDouble(doubleTag: DoubleTag) {
        append(doubleTag.value.toString(), Token.NUMBER)
        if (!ignoreSuffix) appendToken(Token.DOUBLE_SUFFIX)
    }

    fun visitFloat(floatTag: FloatTag) {
        append(floatTag.value.toString(), Token.NUMBER)
        if (!ignoreSuffix) appendToken(Token.FLOAT_SUFFIX)
    }

    fun visitInt(intTag: IntTag) {
        append(intTag.value.toString(), Token.NUMBER)
    }

    fun visitLong(longTag: LongTag) {
        append(longTag.value.toString(), Token.NUMBER)
        if (!ignoreSuffix) appendToken(Token.LONG_SUFFIX)
    }

    fun visitShort(shortTag: ShortTag) {
        append(shortTag.value.toString(), Token.NUMBER)
        if (!ignoreSuffix) appendToken(Token.SHORT_SUFFIX)
    }

    fun visitString(stringTag: StringTag) {
        appendToken(Token.STRING_QUOTE).append(stringTag.value, Token.STRING).appendToken(Token.STRING_QUOTE)
    }

    fun visitCompound(compoundTag: CompoundTag) {
        appendToken(Token.STRUCT_OPEN).line()
        indentCount += 1
        val iterator = compoundTag.entrySet().iterator()
        while (iterator.hasNext()) {
            val (key, value) = iterator.next()
            spaces().appendToken(Token.KEY_QUOTE).append(key, Token.KEY).appendToken(Token.KEY_QUOTE)
            appendToken(Token.COLON).appendToken(Token.SPACE)
            visit(value)
            if (iterator.hasNext()) {
                appendToken(Token.ELEMENT_SEPARATOR)
            }
            line()
        }
        indentCount -= 1
        spaces().appendToken(Token.STRUCT_CLOSE)
    }

    override fun AbstractDataVisualizer.VisualizerToken.color(): Int = when (this) {
        Token.KEY, Token.KEY_QUOTE -> TextColor.AQUA
        Token.NUMBER -> TextColor.GOLD
        Token.STRING, Token.STRING_QUOTE -> TextColor.GREEN
        Token.SHORT_SUFFIX, Token.LONG_SUFFIX, Token.FLOAT_SUFFIX, Token.DOUBLE_SUFFIX, Token.BYTE_SUFFIX -> TextColor.RED
        Token.INT_ARRAY_PREFIX, Token.LONG_ARRAY_PREFIX, Token.BYTE_ARRAY_PREFIX -> TextColor.DARK_RED
        Token.LIST_TYPE_SEPARATOR -> TextColor.DARK_GRAY
        else -> TextColor.WHITE
    }

    enum class Token(override val token: String?) : AbstractDataVisualizer.VisualizerToken {
        ELEMENT_SEPARATOR(","),
        LIST_CLOSE("]"),
        LIST_OPEN("["),
        LIST_TYPE_SEPARATOR(";"),
        STRUCT_CLOSE("}"),
        STRUCT_OPEN("{"),
        COLON(":"),
        BYTE_SUFFIX("b"),
        BYTE_ARRAY_PREFIX("B"),
        SHORT_SUFFIX("s"),
        INT_ARRAY_PREFIX("I"),
        LONG_SUFFIX("L"),
        LONG_ARRAY_PREFIX("L"),
        FLOAT_SUFFIX("f"),
        DOUBLE_SUFFIX("d"),
        KEY_QUOTE("\""),
        STRING_QUOTE("\""),
        SPACE(" "),
        NUMBER(null),
        STRING(null),
        KEY(null),
    }

}
