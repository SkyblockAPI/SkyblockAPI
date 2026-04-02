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
import tech.thatgravyboat.skyblockapi.api.remote.PetQuery
import tech.thatgravyboat.skyblockapi.api.remote.RepoItemsAPI
import tech.thatgravyboat.skyblockapi.api.remote.RepoPetsAPI
import tech.thatgravyboat.skyblockapi.api.remote.RepoRunesAPI
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.UNKNOWN
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.attribute
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.enchantment
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.item
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.pet
import tech.thatgravyboat.skyblockapi.api.remote.api.SkyBlockId.Companion.rune
import tech.thatgravyboat.skyblockapi.utils.builders.ItemBuilder
import tech.thatgravyboat.skyblockapi.utils.extentions.*
import tech.thatgravyboat.skyblockapi.utils.json.getPath

@Module
object SimpleItemAPI {

    internal val unobtainableIds = SkyBlockAPI.getRepo("skyblockid/unobtainable_ids", SkyBlockId.CODEC.listOf())
    private val cache: MutableMap<SkyBlockId, ItemStack?> = mutableMapOf()
    private val nameCache: MutableMap<String, SkyBlockId> = mutableMapOf()
    private val allIds: MutableList<SkyBlockId> = mutableListOf()
    var isFullyCached = false
        private set

    init {
        if (RepoAPI.isInitialized()) setupCache()
    }

    private fun Iterable<Pair<String, SkyBlockId>>.saveIds() = this.apply {
        allIds.addAll(this.map { (_, id) -> id })
    }

    private fun List<Pair<String, SkyBlockId>>.applyFiltered() = nameCache.putAll(this.saveIds().filter { (_, id) -> id !in unobtainableIds }.toMap())

    private fun SkyBlockId.cleanOrNull() = this.cleanId.uppercase().takeUnless { it == UNKNOWN }

    fun findIdByName(name: String) = nameCache[name.lowercase().stripColor()]

    fun getItemByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(SkyBlockId::item)) {
        val itemId = id.cleanOrNull() ?: return@getOrPut null

        return@getOrPut RepoItemsAPI.getItemOrNull(itemId)
    }

    fun getItemById(id: SkyBlockId): ItemStack = getItemByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) {
        name("Unknown item: $id")
    }

    fun getPetByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(SkyBlockId::pet)) {
        val petId = id.cleanOrNull() ?: return@getOrPut null

        if (petId.contains(":")) {
            val (petId, rarity) = petId.split(":")
            val sbRarity = SkyBlockRarity.fromNameOrNull(rarity)
            val pet = sbRarity?.let { RepoPetsAPI.getPetAsItemOrNull(PetQuery(petId, it, 1)) }
            pet?.let { return@getOrPut it }
        }

        return@getOrPut SkyBlockRarity.entries.reversed().firstNotNullOfOrNull { skyBlockRarity ->
            runCatching {
                RepoPetsAPI.getPetAsItemOrNull(PetQuery(petId.substringBefore(":"), skyBlockRarity, 1))
            }.getOrNull()
        }
    }

    fun getPetById(id: SkyBlockId): ItemStack = getPetByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) {
        name("Unknown pet: $id")
    }

    fun getRuneByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(SkyBlockId::rune)) {
        val runeId = id.cleanOrNull() ?: return@getOrPut null

        if (runeId.contains(":")) {
            val (runeId, literalLevel) = runeId.split(":")
            val level = literalLevel.toIntValue()
            RepoRunesAPI.getRuneAsItemOrNull(runeId, level)?.let { return@getOrPut it }
        }

        return@getOrPut RepoRunesAPI.getRuneAsItemOrNull(runeId.substringBefore(":"), null)
    }

    fun getRuneById(id: SkyBlockId) = getRuneByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) {
        name("Unknown rune: $id")
    }

    fun getEnchantmentByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(SkyBlockId::enchantment)) {
        val enchantmentId = id.cleanOrNull() ?: return@getOrPut null

        if (enchantmentId.contains(":")) {
            val (enchantmentId, literalLevel) = enchantmentId.split(":")
            val level = literalLevel.toIntValue()
            RepoEnchantmentAPI.getEnchantmentAsItemOrNull(enchantmentId, level)?.let { return@getOrPut it }
        }

        return@getOrPut RepoEnchantmentAPI.getEnchantmentAsItemOrNull(enchantmentId.substringBefore(":"), null)
    }

    fun getEnchantmentById(id: SkyBlockId): ItemStack = getEnchantmentByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) {
        name("Unknown enchantment: $id")
    }

    fun getAttributeByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(SkyBlockId::attribute)) {
        val attributeId = id.cleanOrNull() ?: return@getOrPut null

        return@getOrPut RepoAttributeAPI.getAttributeByIdOrNull(attributeId)
    }

    fun getAttributeById(id: SkyBlockId): ItemStack = getAttributeByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) {
        name("Unknown attribute: $id")
    }

    fun getPotionByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(SkyBlockId::potion)) {
        val potionId = id.cleanOrNull() ?: return@getOrPut null

        return@getOrPut RepoPotionsAPI.getPotionAsItemOrNull(potionId.substringBefore(":"), null)
    }

    fun getPotionById(id: SkyBlockId): ItemStack = getPotionByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) {
        name("Unknown potion: $id")
    }

    internal fun getUnknownById(id: SkyBlockId): ItemStack? = when {
        id.isPet -> getPetByIdOrNull(id)
        id.isRune -> getRuneByIdOrNull(id)
        id.isEnchantment -> getEnchantmentByIdOrNull(id)
        id.isAttribute -> getAttributeByIdOrNull(id)
        id.isPotion -> getPotionByIdOrNull(id)
        id.isItem -> getItemByIdOrNull(id)
        id.isUnsafe -> getItemByIdOrNull(id)
        else -> null
    }

    fun getAllIds(): List<SkyBlockId> = allIds

    fun getAllNames(): Set<String> = nameCache.keys

    @Subscription(RepoStatusEvent::class)
    @OnRepoStatus(RepoStatus.SUCCESS)
    fun onRepoStatus() {
        setupCache()
    }

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

        RepoAPI.potions().potions().flatMap { (_, potion) ->
            potion.levels.entries.map { (_, level) ->
                "${potion.name()} ${level.literalLevel}" to RepoPotionsAPI.createId(potion.type, potion.internalPotion, level.level)
            }
        }.applyFiltered()

        RepoAPI.items().items().entries.mapNotNull { (id, element) ->
            val components = element.getPath("['components'].['minecraft:custom_name'].['text']") ?: return@mapNotNull null
            components.asString.stripColor() to item(id)
        }.applyFiltered()

        val newCache = nameCache.flatMap { (key, value) ->
            val key = key.lowercase().stripColor()
            listOf(
                key to value,
                key.sanitizeForCommandInput() to value,
            )
        }.distinct().toMap()
        nameCache.clear()
        nameCache.putAll(newCache)
        // Force load all items
        allIds.forEach { it.toItem() }
        SkyBlockAPI.trace("[SimpleItemAPI] Cached ${nameCache.size} item names and ${allIds.size} ids in ${start.since().toReadableTime(allowMs = true)}")
        isFullyCached = true
    }
}
