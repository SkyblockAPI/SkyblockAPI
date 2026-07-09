package tech.thatgravyboat.skyblockapi.api.remote.api

import com.mojang.serialization.Codec
import me.owdding.ktcodecs.IncludedCodec
import net.minecraft.core.component.DataComponents
import net.minecraft.network.codec.StreamCodec
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.datatype.DataType
import tech.thatgravyboat.skyblockapi.api.datatype.DataTypes
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.UNKNOWN
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.neuIdRegex
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.neuPotionRegex
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockIdOverrides.fixHypixelId
import tech.thatgravyboat.skyblockapi.api.remote.api.resolvers.DefaultIdResolver
import tech.thatgravyboat.skyblockapi.api.remote.api.resolvers.IdResolverKind
import tech.thatgravyboat.skyblockapi.api.repo.apis.SkyBlockAttributesRepo
import tech.thatgravyboat.skyblockapi.impl.debug.ItemDebugCategory
import tech.thatgravyboat.skyblockapi.impl.debug.addDebugString
import tech.thatgravyboat.skyblockapi.impl.debug.addStringDebug
import tech.thatgravyboat.skyblockapi.utils.extentions.ItemStack
import tech.thatgravyboat.skyblockapi.utils.extentions.get
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.text.Text
import tech.thatgravyboat.skyblockapi.utils.text.TextColor
import tech.thatgravyboat.skyblockapi.utils.text.TextStyle.color

typealias SkyBlockItemId = SkyBlockId

@JvmInline
value class SkyBlockId private constructor(val id: String) {
    companion object Companion {
        @JvmStatic
        @get:JvmName("getIdResolverKind")
        internal val idResolverKind: ThreadLocal<IdResolverKind> = ThreadLocal.withInitial { IdResolverKind.Unknown }

        private val amountRegex = Regex(".*?x[\\d,]+")
        private val petRegex = Regex("\\[?lvl \\d+]? (.*)")
        internal val neuIdRegex = Regex("\\w+;\\d+")
        internal val neuPotionRegex = Regex("POTION_\\w+")

        const val DELIMITER = ":"
        const val DERIVED = "!"
        const val ITEM = "item$DELIMITER"

        const val PET = "pet$DELIMITER"
        const val RUNE = "rune$DELIMITER"
        const val ATTRIBUTE = "attribute$DELIMITER"
        const val ENCHANTMENT = "enchantment$DELIMITER"
        const val POTION = "potion$DELIMITER"
        const val UNSAFE = "unsafe$DELIMITER"
        const val UNKNOWN_VALUE = "unknown"
        const val UNKNOWN = "sbapi${DELIMITER}$UNKNOWN_VALUE"
        val EMPTY: SkyBlockId = item(UNKNOWN)

        fun item(id: String) = SkyBlockId("$ITEM$id".lowercase())
        fun pet(id: String) = SkyBlockId("$PET$id".lowercase())
        fun pet(id: String, rarity: String) = SkyBlockId("$PET$id$DELIMITER$rarity".lowercase())
        fun pet(id: String, rarity: SkyBlockRarity) = pet(id, rarity.name)
        fun rune(id: String) = SkyBlockId("$RUNE$id".lowercase())
        fun rune(id: String, level: Int) = SkyBlockId("$RUNE$id$DELIMITER$level".lowercase())
        fun attribute(id: String) = SkyBlockId("$ATTRIBUTE$id".lowercase())
        fun enchantment(id: String) = SkyBlockId("$ENCHANTMENT$id".lowercase())
        fun enchantment(id: String, level: Int) = SkyBlockId("$ENCHANTMENT$id$DELIMITER$level".lowercase())
        fun potion(id: String) = SkyBlockId("$POTION$id".lowercase())
        fun potion(id: String, level: Int) = SkyBlockId("$POTION$id$DELIMITER$level".lowercase())
        fun potion(type: String?, internalPotion: String?, level: Int?): SkyBlockId = when {
            type == null -> potion("water")
            type != "POTION" -> potion(type)
            internalPotion == null -> potion(UNKNOWN)
            level != null -> potion(internalPotion, level)
            else -> potion(internalPotion)
        }

        fun fromItem(item: ItemStack) = item.getSbId()

        fun fromName(name: String, dropLast: Boolean = true): SkyBlockId? {
            var name = name.lowercase().stripColor()
            if (name.matches(petRegex)) {
                name = name.replace(petRegex, "$1")
            } else if (name.matches(amountRegex)) {
                name = name.substringBeforeLast(" x")
            } else if (name.endsWith(" minion")) {
                name = "$name i"
            }

            return SimpleItemAPI.findIdByName(name.trim()) ?: if (dropLast) SimpleItemAPI.findIdByName(name.substringBeforeLast(" ").trim()) else null
        }

        fun unknownType(input: String): SkyBlockId? {
            val unsafeId = unsafe(input.lowercase())

            fun <T> safe(init: () -> T): T? {
                return runCatching { init() }.getOrNull()
            }

            safe { SimpleItemAPI.getLazyItemStackForItem(unsafeId) }?.let { return item(input) }
            safe { SimpleItemAPI.getLazyItemStackForPet(unsafeId) }?.let { return pet(input) }
            safe { SimpleItemAPI.getLazyItemStackForEnchantment(unsafeId) }?.let { return enchantment(input) }
            safe { SimpleItemAPI.getLazyItemStackForAttribute(unsafeId) }?.let { return attribute(input) }
            safe { SimpleItemAPI.getLazyItemStackForRune(unsafeId) }?.let { return rune(input) }
            safe { SimpleItemAPI.getPotionByIdOrNull(unsafeId) }?.let { return potion(input) }

            return null
        }

        fun unsafe(id: String) = SkyBlockId("$UNSAFE$id")

        @IncludedCodec
        val CODEC: Codec<SkyBlockId> = Codec.STRING.xmap({ it.lowercase() }, { it }).xmap(::SkyBlockId, SkyBlockId::rootId)

        val UNKNOWN_CODEC: Codec<SkyBlockId> = Codec.STRING.xmap({ it.lowercase() }, { it }).xmap({ unknownType(it) ?: SkyBlockId(it) }, { it.rootId })

        fun ItemStack.getSkyBlockId(): SkyBlockId? = this[DataTypes.SKYBLOCK_ID] ?: createIdForItem(this)

        private data object SkyblockIdResolver : ItemDebugCategory

        internal fun createIdForItem(stack: ItemStack): SkyBlockId? {
            val kind = idResolverKind.get()
            stack.addStringDebug(SkyblockIdResolver) { "Using $kind resolvers" }
            return kind.entries().firstNotNullOfOrNull {
                stack.addStringDebug(SkyblockIdResolver) { "Trying $it" }
                it.tryResolve(stack, kind)
            }
        }

        @JvmStatic
        @JvmName("wrap")
        internal fun <T : Any, D : Any> wrap(codec: StreamCodec<T, D>, kind: IdResolverKind) = object : StreamCodec<T, D> {
            override fun decode(first: T): D {
                return try {
                    idResolverKind.set(kind)
                    codec.decode(first)
                } finally {
                    idResolverKind.set(IdResolverKind.Unknown)
                }
            }

            override fun encode(first: T, second: D) = codec.encode(first, second)
        }
    }

    fun asDerived(derived: Boolean = true) = when {
        this.isDerived && !derived -> SkyBlockId(this.id.removeDerived())
        !this.isDerived && derived -> SkyBlockId("$DERIVED${this.id}")
        else -> this
    }

    val isDerived get() = this.id.startsWith(DERIVED)
    val rootId get() = this.id.removeDerived()

    private fun String.removeDerived() = this.removePrefix(DERIVED)

    val isItem: Boolean get() = id.removeDerived().startsWith(ITEM)
    val isPet: Boolean get() = id.removeDerived().startsWith(PET)
    val isRune: Boolean get() = id.removeDerived().startsWith(RUNE)
    val isEnchantment: Boolean get() = id.removeDerived().startsWith(ENCHANTMENT)
    val isAttribute: Boolean get() = id.removeDerived().startsWith(ATTRIBUTE)
    val isPotion: Boolean get() = id.removeDerived().startsWith(POTION)
    val isUnsafe: Boolean get() = id.removeDerived().startsWith(UNSAFE)
    val cleanId: String get() = id.removeDerived().substringAfter(DELIMITER)
    val skyblockId: String
        get() = fixHypixelId() ?: when {

            isPet -> cleanId.substringBeforeLast(DELIMITER)
            isEnchantment -> {
                "ENCHANTED_BOOK_${cleanId.substringBeforeLast(DELIMITER)}_${cleanId.substringAfterLast(DELIMITER)}"
            }

            isAttribute -> {
                "${SkyBlockAttributesRepo.get(cleanId)?.shardName()}_SHARD"
            }

            else -> cleanId
        }.uppercase()
    val bazaarId: String
        get() = when {
            isAttribute -> SkyBlockAttributesRepo.get(cleanId)?.shardId ?: "UNKNOWN"
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
        isPotion -> getPotion()

        else -> ItemStack(Items.BARRIER) {
            set(DataComponents.CUSTOM_NAME, Text.of(id) { this.color = TextColor.RED })
        }
    }

    private fun getItem(): ItemStack = SimpleItemAPI.getItemById(this)
    private fun getPet(): ItemStack = SimpleItemAPI.getPetById(this)
    private fun getRune(): ItemStack = SimpleItemAPI.getRuneById(this)
    private fun getEnchantment(): ItemStack = SimpleItemAPI.getEnchantmentById(this)
    private fun getAttribute(): ItemStack = SimpleItemAPI.getAttributeById(this)
    private fun getPotion(): ItemStack = SimpleItemAPI.getPotionById(this)

    override fun toString() = "SkyBlockId($id)"
}

private fun ItemStack.getSbId(): SkyBlockId? {
    operator fun <Type> DataType<Type>.invoke() = this.resolve(this@getSbId)

    val id = DataTypes.ID() ?: run {
        addStringDebug(DefaultIdResolver) { "Item doesn't have id" }
        return null
    }

    return when (id) {
        "ATTRIBUTE_SHARD" -> {
            val attributes = DataTypes.ATTRIBUTES()
            addStringDebug(DefaultIdResolver) { "Trying attributes $attributes" }
            attributes?.entries?.firstOrNull()?.let { (key, _) -> SkyBlockAttributesRepo.get(key)?.attributeId }
                .let { it ?: UNKNOWN }.let(SkyBlockId::attribute)
        }

        "ABICASE" -> {
            val model = DataTypes.ABICASE_MODEL()
            addStringDebug(DefaultIdResolver) { "Resolving to abicase $model" }
            model?.let { SkyBlockId.item("abicase_$it") }
        }

        "PARTY_HAT_CRAB_ANIMATED" -> {
            val hatColor = DataTypes.PARTY_HAT_COLOR()
            addStringDebug(DefaultIdResolver) { "Resolving animated party hat crab $hatColor" }
            hatColor?.let { SkyBlockId.item("party_hat_crab_${it}_animated") }
        }
        "PARTY_HAT_CRAB" -> {
            val hatColor = DataTypes.PARTY_HAT_COLOR()
            addStringDebug(DefaultIdResolver) { "Resolving party hat crab $hatColor" }
            hatColor?.let { SkyBlockId.item("party_hat_crab_$it") }
        }

        "CAKE_HAT_2026" -> {
            val hatColor = DataTypes.PARTY_HAT_COLOR()
            addStringDebug(DefaultIdResolver) { "Resolving party hat crab $hatColor" }
            hatColor?.let { SkyBlockId.item("cake_hat_2026_$it") }
        }

        else if (id == "POTION" || neuPotionRegex.matches(id)) -> {
            val type = DataTypes.POTION_TYPE()
            val internalPotion = DataTypes.POTION()
            val level = DataTypes.POTION_LEVEL()
            addStringDebug(DefaultIdResolver) { "Resolving potion type: $type, internalPotion: $internalPotion, level: $level" }
            SkyBlockId.potion(type, internalPotion, level)
        }

        else if (id == "ENCHANTED_BOOK" || (this.`is`(Items.ENCHANTED_BOOK) && neuIdRegex.matches(id))) -> {
            val enchants = DataTypes.ENCHANTMENTS()?.entries

            addStringDebug(DefaultIdResolver) { "Resolving enchantments $enchants" }
            when (enchants?.size) {
                null, 0 -> SkyBlockId.enchantment(UNKNOWN)
                1 -> enchants.first().let { (key, value) -> SkyBlockId.enchantment(key.lowercase(), value) }
                else -> SkyBlockId.item("enchanted_book")
            }
        }

        else if (id == "PET" || id == "RUNE" || id == "UNIQUE_RUNE" || (this.`is`(Items.PLAYER_HEAD) && neuIdRegex.matches(id))) -> {
            DataTypes.USED_RUNE() ?: DataTypes.PET_DATA()?.let { (id, _, _, rarity) ->
                SkyBlockId.pet(id, rarity)
            } ?: SkyBlockId.pet(UNKNOWN)
        }

        else -> SkyBlockId.item(id)
    }
}
