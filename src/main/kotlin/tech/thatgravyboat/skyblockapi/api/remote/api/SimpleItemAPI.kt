package tech.thatgravyboat.skyblockapi.api.remote.api

import me.owdding.ktmodules.Module
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.repolib.api.RepoStatus
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
import tech.thatgravyboat.skyblockapi.api.events.base.Subscription
import tech.thatgravyboat.skyblockapi.api.events.base.predicates.OnRepoStatus
import tech.thatgravyboat.skyblockapi.api.events.misc.RepoStatusEvent
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.UNKNOWN
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.attribute
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.enchantment
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.item
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.pet
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.rune
import tech.thatgravyboat.skyblockapi.api.repo.LazyItemStack
import tech.thatgravyboat.skyblockapi.api.repo.apis.*
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.getPath

@Module
object SimpleItemAPI {

    internal val unobtainableIds = SkyBlockAPI.getRepo("skyblockid/unobtainable_ids", SkyBlockId.CODEC.listOf())
    private val cache = object : RepoItemCache<SkyBlockId>("Items") {
        override fun create(key: SkyBlockId): LazyItemStack? {
            val clean = key.cleanId.uppercase().takeUnless { it == UNKNOWN } ?: return null

            return when {
                key.isPet -> {
                    if (clean.contains(":")) {
                        val (petId, rarity) = clean.split(":")
                        val sbRarity = SkyBlockRarity.fromNameOrNull(rarity)
                        if (sbRarity != null) {
                            val pet = SkyBlockPetsRepo.getLazyItemStack {
                                this.id = petId
                                this.rarity = sbRarity
                                this.level = 1
                            }

                            if (pet != null) {
                                return pet
                            }
                        }
                    }

                    SkyBlockRarity.entries.reversed().firstNotNullOfOrNull { rarity ->
                        runCatching {
                            SkyBlockPetsRepo.getLazyItemStack {
                                this.id = clean
                                this.rarity = rarity
                                this.level = 1
                            }
                        }.getOrNull()
                    }
                }
                key.isRune -> SkyBlockRunesRepo.getLazyItemStack {
                    if (clean.contains(":")) {
                        val (runeId, level) = clean.split(":")
                        this.id = runeId
                        this.tier = level.toIntOrNull()
                    } else {
                        this.id = clean
                    }
                }
                key.isEnchantment -> SkyBlockEnchantmentsRepo.getLazyItemStack {
                    if (clean.contains(":")) {
                        val (enchantmentId, level) = clean.split(":")
                        this.id = enchantmentId
                        this.level = level.toIntOrNull()
                    } else {
                        this.id = clean
                    }
                }
                key.isAttribute -> SkyBlockAttributesRepo.getLazyItemStack(clean)
                key.isItem -> clean.let(SkyBlockItemsRepo::getLazyItemStack)
                key.isUnsafe -> clean.let(SkyBlockItemsRepo::getLazyItemStack)
                else -> null
            }
        }

        public override fun clear() {
            super.clear()
        }
    }
    private val names: MutableMap<String, SkyBlockId> = mutableMapOf()
    private val ids: MutableList<SkyBlockId> = mutableListOf()

    init {
        if (RepoAPI.isInitialized()) setupCache()
    }

    fun findIdByName(name: String) = names[name.lowercase().stripColor()]

    fun getLazyItemStackForItem(id: SkyBlockId): LazyItemStack? = cache.getLazyItemStack(id.trySafe(::item))
    fun getItemByIdOrNull(id: SkyBlockId): ItemStack? = getLazyItemStackForItem(id)?.create()
    fun getItemById(id: SkyBlockId): ItemStack = getItemByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) { name("Unknown item: $id") }

    fun getLazyItemStackForPet(id: SkyBlockId): LazyItemStack? = cache.getLazyItemStack(id.trySafe(::pet))
    fun getPetByIdOrNull(id: SkyBlockId): ItemStack? = getLazyItemStackForPet(id)?.create()
    fun getPetById(id: SkyBlockId): ItemStack = getPetByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) { name("Unknown pet: $id") }

    fun getLazyItemStackForRune(id: SkyBlockId): LazyItemStack? = cache.getLazyItemStack(id.trySafe(::rune))
    fun getRuneByIdOrNull(id: SkyBlockId): ItemStack? = getLazyItemStackForRune(id)?.create()
    fun getRuneById(id: SkyBlockId) = getRuneByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) { name("Unknown rune: $id") }

    fun getLazyItemStackForEnchantment(id: SkyBlockId): LazyItemStack? = cache.getLazyItemStack(id.trySafe(::enchantment))
    fun getEnchantmentByIdOrNull(id: SkyBlockId): ItemStack? = getLazyItemStackForEnchantment(id)?.create()
    fun getEnchantmentById(id: SkyBlockId): ItemStack = getEnchantmentByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) { name("Unknown enchantment: $id") }

    fun getLazyItemStackForAttribute(id: SkyBlockId): LazyItemStack? = cache.getLazyItemStack(id.trySafe(::attribute))
    fun getAttributeByIdOrNull(id: SkyBlockId): ItemStack? = getLazyItemStackForAttribute(id)?.create()
    fun getAttributeById(id: SkyBlockId): ItemStack = getAttributeByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) { name("Unknown attribute: $id") }

    fun getAllIds(): List<SkyBlockId> = ids
    fun getAllNames(): Set<String> = names.keys

    @Subscription(RepoStatusEvent::class)
    @OnRepoStatus(RepoStatus.SUCCESS)
    fun onRepoStatus() {
        setupCache()
    }

    private fun List<Pair<String, SkyBlockId>>.applyFiltered() = this
        .apply { ids.addAll(this.map { (_, id) -> id }) }
        .filter { (_, id) -> id !in unobtainableIds }
        .toMap()
        .let(names::putAll)

    private fun setupCache() {
        val start = currentInstant()
        cache.clear()
        RepoAPI.pets().pets().entries.map { (id, data) -> data.name() to pet(id) }.applyFiltered()

        RepoAPI.runes().runes().entries.flatMap { (id, data) ->
            data.mapNotNull { rune ->
                rune.name().stripColor() to rune("$id", rune.tier())
            }
        }.applyFiltered()


        RepoAPI.enchantments().enchantments().flatMap { (id, enchantments) ->
            enchantments.levels().map { (_, enchantment) ->
                "${enchantments.name()} ${enchantment.literalLevel()}" to enchantment("$id:${enchantment.level()}")
            }
        }.applyFiltered()

        RepoAPI.attributes().attributes().flatMap { (_, attribute) ->
            listOf(
                attribute.name() to attribute(attribute.attributeId()),
                attribute.shardName() to attribute(attribute.attributeId()),
                attribute.shardName().removeSuffix("Shard").trim() to attribute(attribute.attributeId()),
            )
        }.applyFiltered()

        RepoAPI.items().items().entries.mapNotNull { (id, element) ->
            val components = element.getPath("['components'].['minecraft:custom_name'].['text']") ?: return@mapNotNull null
            components.asString.stripColor() to item(id)
        }.applyFiltered()

        val newCache = names.flatMap { (key, value) ->
            val key = key.lowercase().stripColor()
            listOf(
                key to value,
                key.sanitizeForCommandInput() to value,
            )
        }.distinct().toMap()
        names.clear()
        names.putAll(newCache)
        SkyBlockAPI.trace("[SimpleItemAPI] Cached ${names.size} item names and ${ids.size} ids in ${start.since().toReadableTime(allowMs = true)}")
    }
}
