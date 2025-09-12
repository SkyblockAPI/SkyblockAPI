package tech.thatgravyboat.skyblockapi.api.remote.api

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import me.owdding.ktmodules.Module
import net.minecraft.core.component.DataComponents
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.defaults.GenericDataTypes
import tech.thatgravyboat.skyblockapi.api.datatype.getData
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.DELIMITER
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.UNKNOWN
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockIdOverrides.fixHypixelId
import tech.thatgravyboat.skyblockapi.utils.extentions.ItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextProperties.stripped
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

@JvmInline
value class SkyBlockId private constructor(val id: String) {
    @Module
    companion object Companion {
        private val amountRegex = Regex(".*?x[\\d,]+")
        private val petRegex = Regex("\\[?lvl \\d+]? (.*)")

        const val DELIMITER = ":"
        const val ITEM = "item$DELIMITER"

        const val PET = "pet$DELIMITER"
        const val RUNE = "rune$DELIMITER"
        const val ATTRIBUTE = "attribute$DELIMITER"
        const val ENCHANTMENT = "enchantment$DELIMITER"
        const val UNSAFE = "unsafe$DELIMITER"
        const val UNKNOWN = "ocean${DELIMITER}unknown"
        val EMPTY: SkyBlockId = item(UNKNOWN)

        fun item(id: String) = SkyBlockId("$ITEM$id".lowercase())
        fun pet(id: String) = SkyBlockId("$PET$id".lowercase())
        fun pet(id: String, rarity: String) = SkyBlockId("$PET$id$DELIMITER$rarity".lowercase())
        fun rune(id: String) = SkyBlockId("$RUNE$id".lowercase())
        fun rune(id: String, level: Int) = SkyBlockId("$RUNE$id$DELIMITER$level".lowercase())
        fun attribute(id: String) = SkyBlockId("$ATTRIBUTE$id".lowercase())
        fun enchantment(id: String) = SkyBlockId("$ENCHANTMENT$id".lowercase())
        fun enchantment(id: String, level: Int) = SkyBlockId("$ENCHANTMENT$id$DELIMITER$level".lowercase())

        fun fromItem(item: ItemStack) = item.getSbId()

        fun fromName(name: String, dropLast: Boolean = true): SkyBlockId? {
            var name = name.lowercase().stripColor()
            if (name.matches(petRegex)) {
                name = name.replace(petRegex, "$1")
            } else if (name.matches(amountRegex)) {
                name = name.substringBeforeLast(" x")
            }

            return SimpleItemAPI.findIdByName(name.trim()) ?: if (dropLast) SimpleItemAPI.findIdByName(name.substringBeforeLast(" ").trim()) else null
        }

        fun unknownType(input: String): SkyBlockId? {
            val unsafeId = unsafe(input.lowercase())

            fun <T> safe(init: () -> T): T? {
                return runCatching { init() }.getOrNull()
            }

            safe { SimpleItemAPI.getItemByIdOrNull(unsafeId) }?.let { return item(input) }
            safe { SimpleItemAPI.getPetByIdOrNull(unsafeId) }?.let { return pet(input) }
            safe { SimpleItemAPI.getEnchantmentByIdOrNull(unsafeId) }?.let { return enchantment(input) }
            safe { SimpleItemAPI.getAttributeByIdOrNull(unsafeId) }?.let { return attribute(input) }
            safe { SimpleItemAPI.getRuneByIdOrNull(unsafeId) }?.let { return rune(input) }

            return null
        }

        fun unsafe(id: String) = SkyBlockId("$UNSAFE$id")

        @IncludedCodec
        val CODEC: Codec<SkyBlockId> = Codec.STRING
            .xmap({ it.lowercase() }, { it })
            .xmap(::SkyBlockId, SkyBlockId::id)

        val UNKNOWN_CODEC: Codec<SkyBlockId> = Codec.STRING.xmap({ it.lowercase() }, { it })
            .xmap({ unknownType(it) ?: SkyBlockId(it) }, { it.id })

        fun ItemStack.getSkyBlockId() = this[DataTypes.SKYBLOCK_ID] ?: fromName(this.hoverName.stripped) ?: fromItem(this)
    }

    val isItem: Boolean get() = id.startsWith(ITEM)
    val isPet: Boolean get() = id.startsWith(PET)
    val isRune: Boolean get() = id.startsWith(RUNE)
    val isEnchantment: Boolean get() = id.startsWith(ENCHANTMENT)
    val isAttribute: Boolean get() = id.startsWith(ATTRIBUTE)
    val isUnsafe: Boolean get() = id.startsWith(UNSAFE)
    val cleanId: String get() = id.substringAfter(DELIMITER)
    val skyblockId: String
        get() = fixHypixelId() ?: when {

            isPet -> cleanId.substringBeforeLast(DELIMITER)
            isEnchantment -> {
                "ENCHANTED_BOOK_${cleanId.substringBeforeLast(DELIMITER)}_${cleanId.substringAfterLast(DELIMITER)}"
            }

            isAttribute -> {
                "${RepoAttributeAPI.getAttributeDataById(cleanId)?.shardName()}_SHARD"
            }

            else -> cleanId
        }.uppercase()
    val bazaarId: String
        get() = when {

            isAttribute -> "SHARD_${RepoAttributeAPI.getAttributeDataById(cleanId)?.shardName()}"
            isEnchantment -> "ENCHANTMENT_${cleanId.substringBeforeLast(DELIMITER)}_${cleanId.substringAfterLast(DELIMITER)}"

            else -> skyblockId
        }

    fun trySafe(consumer: (String) -> SkyBlockId): SkyBlockId = if (isUnsafe) consumer(cleanId) else this

    fun toItem(): ItemStack = when {

        isRune -> getRune()
        isPet -> getPet()
        isItem -> getItem()
        isEnchantment -> getEnchantment()
        isAttribute -> getAttribute()

        else -> ItemStack(Items.BARRIER) {
            set(DataComponents.CUSTOM_NAME, Text.of(id) { this.color = TextColor.RED })
        }
    }

    private fun getItem(): ItemStack = SimpleItemAPI.getItemById(this)
    private fun getPet(): ItemStack = SimpleItemAPI.getPetById(this)
    private fun getRune(): ItemStack = SimpleItemAPI.getRuneById(this)
    private fun getEnchantment(): ItemStack = SimpleItemAPI.getEnchantmentById(this)
    private fun getAttribute(): ItemStack = SimpleItemAPI.getAttributeById(this)

}

private fun ItemStack.getSbId(): SkyBlockId? {
    val data = this.getData(DataTypes.ID) ?: GenericDataTypes.ID.factory(this)
    return when (data) {
        "RUNE", "UNIQUE_RUNE" -> {
            this.getData(DataTypes.APPLIED_RUNE)?.let { (rune, level) -> "$rune$DELIMITER$level" }.let { it ?: UNKNOWN }
                .let(SkyBlockId::rune)
        }

        "PET" -> {
            this.getData(DataTypes.PET_DATA)?.let { (id, _, _, rarity) -> "$id$DELIMITER${rarity.name}" }.let { it ?: UNKNOWN }
                .let(SkyBlockId::pet)
        }

        "ENCHANTED_BOOK" -> {
            this.getData(DataTypes.ENCHANTMENTS)?.entries?.firstOrNull()?.let { (key, value) -> "$key$DELIMITER$value" }
                .let { it ?: UNKNOWN }
                .let(SkyBlockId::enchantment)
        }

        "ATTRIBUTE_SHARD" -> {
            this.getData(DataTypes.ATTRIBUTES)?.entries?.firstOrNull()?.let { (key, _) -> key }
                .let { it ?: UNKNOWN }.let(SkyBlockId::attribute)
        }

        else -> (data)?.let(SkyBlockId::item)
    }
}
