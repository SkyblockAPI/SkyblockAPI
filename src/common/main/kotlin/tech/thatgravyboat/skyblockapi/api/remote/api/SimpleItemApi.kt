package tech.thatgravyboat.skyblockapi.api.remote.api

import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import tech.thatgravyboat.repolib.api.RepoAPI
import tech.thatgravyboat.skyblockapi.api.SkyBlockAPI
import tech.thatgravyboat.skyblockapi.api.data.SkyBlockRarity
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
import tech.thatgravyboat.skyblockapi.utils.extentions.sanitizeForCommandInput
import tech.thatgravyboat.skyblockapi.utils.extentions.stripColor
import tech.thatgravyboat.skyblockapi.utils.extentions.toIntValue
import tech.thatgravyboat.skyblockapi.utils.extentions.toReadableTime
import tech.thatgravyboat.skyblockapi.utils.json.getPath
import tech.thatgravyboat.skyblockapi.utils.time.currentInstant
import tech.thatgravyboat.skyblockapi.utils.time.since

object SimpleItemApi {

    private val cache: MutableMap<SkyBlockId, ItemStack?> = mutableMapOf()
    private val nameCache: MutableMap<String, SkyBlockId> = mutableMapOf()

    init {
        val start = currentInstant()
        RepoAPI.pets().pets().entries.associate { (id, data) -> data.name() to pet(id) }
            .let(nameCache::putAll)

        RepoAPI.runes().runes().entries.flatMap { (id, data) ->
            data.map { rune ->
                rune.name().stripColor() to rune("$id:${rune.tier()}")
            }
        }.toMap().let(nameCache::putAll)

        RepoAPI.enchantments().enchantments().flatMap { (id, enchantments) ->
            enchantments.levels().map { (level, enchantment) ->
                "${enchantments.name()} ${enchantment.literalLevel()}" to enchantment("$id:${enchantment.level()}")
            }
        }.toMap().let(nameCache::putAll)

        RepoAPI.attributes().attributes().flatMap { (id, attribute) ->
            listOf(
                attribute.name() to attribute(attribute.id()),
                attribute.shardName() to attribute(attribute.id()),
            )
        }.toMap().let(nameCache::putAll)

        RepoAPI.items().items().entries.mapNotNull { (id, element) ->
            val components =
                element.getPath("['components'].['minecraft:custom_name'].['text']") ?: return@mapNotNull null
            components.asString.stripColor() to item(id)
        }.toMap().let(nameCache::putAll)

        val newCache = nameCache.mapKeys { (key) -> key.lowercase().stripColor() }
            .entries.flatMap { (key, value) ->
                listOf(
                    key to value,
                    key.sanitizeForCommandInput() to value,
                )
            }.distinct().toMap()
        nameCache.clear()
        nameCache.putAll(newCache)
        SkyBlockAPI.trace("Cached ${nameCache.size} item names in ${start.since().toReadableTime(allowMs = true)}")
    }

    fun findIdByName(name: String) = nameCache[name.lowercase().stripColor()]

    fun getItemByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(::item)) {
        val itemId = id.cleanId.uppercase()

        if (itemId == UNKNOWN) {
            return null
        }

        return RepoItemsAPI.getItemOrNull(itemId)
    }

    fun getItemById(id: SkyBlockId): ItemStack = getItemByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) {
        name("Unknown item: $id")
    }

    fun getPetByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(::pet)) {
        val petId = id.cleanId.uppercase()

        if (petId == UNKNOWN) {
            return@getOrPut null
        }

        if (petId.contains(":")) {
            val (petId, rarity) = petId.split(":")
            val sbRarity = runCatching { SkyBlockRarity.valueOf(rarity) }.getOrNull()
            val pet = runCatching {
                sbRarity?.let { RepoPetsAPI.getPetAsItemOrNull(PetQuery(petId, it, 1)) }
            }.getOrNull()
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

    fun getRuneByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(::rune)) {
        val runeId = id.cleanId.uppercase()

        if (runeId == UNKNOWN) {
            return@getOrPut null
        }

        if (runeId.contains(":")) {
            val (runeId, literalLevel) = runeId.split(":")
            val level = literalLevel.toIntValue()
            RepoRunesAPI.getRuneAsItemOrNull(runeId, level)?.let { return@getOrPut it }
        }

        for (i in 3 downTo 0) {
            RepoRunesAPI.getRuneAsItemOrNull(runeId.substringBefore(":"), i)?.let { return@getOrPut it }
        }

        return@getOrPut null
    }

    fun getRuneById(id: SkyBlockId) = getRuneByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) {
        name("Unknown rune: $id")
    }

    fun getEnchantmentByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(::enchantment)) {
        val enchantmentId = id.cleanId.uppercase()

        if (enchantmentId == UNKNOWN) {
            return@getOrPut null
        }

        if (enchantmentId.contains(":")) {
            val (enchantmentId, literalLevel) = enchantmentId.split(":")
            val level = literalLevel.toIntValue()
            RepoEnchantmentApi.getEnchantmentAsItemOrNull(enchantmentId, level)?.let { return@getOrPut it }
        }

        for (i in 10 downTo 0) {
            RepoEnchantmentApi.getEnchantmentAsItemOrNull(enchantmentId.substringBefore(":"), i)?.let { return@getOrPut it }
        }

        return@getOrPut null
    }

    fun getEnchantmentById(id: SkyBlockId): ItemStack = getEnchantmentByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) {
        name("Unknown enchantment: $id")
    }

    fun getAttributeByIdOrNull(id: SkyBlockId): ItemStack? = cache.getOrPut(id.trySafe(::attribute)) {
        val attributeId = id.cleanId.uppercase()

        if (attributeId == UNKNOWN) {
            return@getOrPut null
        }

        RepoAttributeApi.getAttributeByIdOrNull(attributeId)?.let { return@getOrPut it }

        return@getOrPut null
    }

    fun getAttributeById(id: SkyBlockId): ItemStack = getAttributeByIdOrNull(id) ?: ItemBuilder(Items.BARRIER) {
        name("Unknown attribute: $id")
    }

}
